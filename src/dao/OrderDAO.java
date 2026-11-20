package dao;

import database.DBConnection;
import model.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDAO {
    public OrderResult saveOrder(int userId, List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cannot save an empty order.");
        }

        String customerSql = "SELECT full_name, address FROM users WHERE user_id = ?";
        String menuSql = "SELECT mi.price, mi.is_available, r.restaurant_name " +
                "FROM menu_items mi JOIN restaurants r ON r.restaurant_id = mi.restaurant_id " +
                "WHERE mi.menu_id = ?";
        String orderSql = "INSERT INTO orders(user_id, delivery_address, subtotal, delivery_fee, discount, total_amount) " +
                "VALUES(?,?,?,?,?,?)";
        String itemSql = "INSERT INTO order_items(order_id, menu_id, quantity, unit_price) VALUES(?,?,?,?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String customerName;
                String address;
                try (PreparedStatement customer = con.prepareStatement(customerSql)) {
                    customer.setInt(1, userId);
                    try (ResultSet rs = customer.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Customer account was not found.");
                        customerName = rs.getString("full_name");
                        address = rs.getString("address");
                    }
                }

                BigDecimal subtotal = BigDecimal.ZERO;
                try (PreparedStatement menu = con.prepareStatement(menuSql)) {
                    for (CartItem cartItem : cart) {
                        if (cartItem.getQuantity() <= 0) {
                            throw new IllegalArgumentException("Item quantities must be positive.");
                        }
                        menu.setInt(1, cartItem.getMenuId());
                        try (ResultSet rs = menu.executeQuery()) {
                            if (!rs.next()) throw new SQLException(
                                    "Menu item no longer exists: " + cartItem.getItemName());
                            if (!rs.getBoolean("is_available")) {
                                throw new IllegalStateException(
                                        "Menu item is unavailable: " + cartItem.getItemName());
                            }
                            cartItem.setPrice(rs.getBigDecimal("price"));
                            cartItem.setRestaurantName(rs.getString("restaurant_name"));
                            subtotal = subtotal.add(cartItem.getTotal());
                        }
                    }
                }

                BigDecimal deliveryFee = subtotal.compareTo(new BigDecimal("3000")) >= 0
                        ? BigDecimal.ZERO : new BigDecimal("100");
                BigDecimal discount = BigDecimal.ZERO;
                BigDecimal total = subtotal.add(deliveryFee).subtract(discount);
                int orderId;

                try (PreparedStatement order = con.prepareStatement(
                        orderSql, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement item = con.prepareStatement(itemSql)) {
                    order.setInt(1, userId);
                    order.setString(2, address == null ? "" : address);
                    order.setBigDecimal(3, subtotal);
                    order.setBigDecimal(4, deliveryFee);
                    order.setBigDecimal(5, discount);
                    order.setBigDecimal(6, total);
                    order.executeUpdate();

                    try (ResultSet keys = order.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Order ID was not generated.");
                        orderId = keys.getInt(1);
                    }

                    for (CartItem cartItem : cart) {
                        item.setInt(1, orderId);
                        item.setInt(2, cartItem.getMenuId());
                        item.setInt(3, cartItem.getQuantity());
                        item.setBigDecimal(4, cartItem.getPrice());
                        item.addBatch();
                    }
                    item.executeBatch();
                }
                con.commit();
                return new OrderResult(orderId, customerName, LocalDateTime.now(),
                        subtotal, deliveryFee, discount, total);
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
            if (e instanceof IllegalStateException) throw (IllegalStateException) e;
            throw new IllegalStateException("Order could not be saved; transaction rolled back.", e);
        }
    }

    public static final class OrderResult {
        private final int orderId;
        private final String customerName;
        private final LocalDateTime orderDate;
        private final BigDecimal subtotal;
        private final BigDecimal deliveryFee;
        private final BigDecimal discount;
        private final BigDecimal total;

        public OrderResult(int orderId, String customerName, LocalDateTime orderDate,
                           BigDecimal subtotal, BigDecimal deliveryFee,
                           BigDecimal discount, BigDecimal total) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.orderDate = orderDate;
            this.subtotal = subtotal;
            this.deliveryFee = deliveryFee;
            this.discount = discount;
            this.total = total;
        }

        public int getOrderId() { return orderId; }
        public String getCustomerName() { return customerName; }
        public LocalDateTime getOrderDate() { return orderDate; }
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getDeliveryFee() { return deliveryFee; }
        public BigDecimal getDiscount() { return discount; }
        public BigDecimal getTotal() { return total; }
    }
}
