package gui;

import dao.OrderDAO;
import model.CartItem;
// import ui.Theme;
import util.CartManager;
import util.Session;
import util.WindowHelper;
import ui.RoundedButton;
import javax.swing.border.EmptyBorder;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ReceiptFrame extends JFrame {
    private final JTextArea receipt = new JTextArea();

    public ReceiptFrame() {
        setTitle("Order Checkout");
        setSize(650, 650);
        setLocationRelativeTo(null);
        WindowHelper.allowSafeClose(this);
        receipt.setEditable(false);
        receipt.setFont(new Font("Monospaced", Font.PLAIN, 15));
        renderPreview();

        RoundedButton placeOrder = new RoundedButton("Place Combined Order");
        placeOrder.addActionListener(e -> placeOrder());
        RoundedButton backHome = new RoundedButton("Back to Home");
        backHome.addActionListener(e -> { dispose(); new HomeFrame(); });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBorder(new EmptyBorder(6, 6, 6, 6));
        buttons.add(backHome);
        buttons.add(placeOrder);
        add(new JScrollPane(receipt), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void renderPreview() {
        receipt.setText("FOOD ORDERING SYSTEM\n\nORDER PREVIEW\n\n");
        appendItems();
        receipt.append("\nSUBTOTAL: Rs. " + CartManager.getTotal());
    }

    private void appendItems() {
        for (Map.Entry<String, List<CartItem>> group : CartManager.groupByRestaurant().entrySet()) {
            receipt.append(group.getKey() + "\n");
            receipt.append("-----------------------------\n");
            for (CartItem item : group.getValue()) {
                receipt.append(String.format("%-22s %3d x Rs. %s = Rs. %s%n",
                        item.getItemName(), item.getQuantity(), item.getPrice(), item.getTotal()));
            }
            receipt.append("Restaurant subtotal: Rs. "
                    + CartManager.getRestaurantTotal(group.getValue()) + "\n\n");
        }
    }

    private void placeOrder() {
        if (Session.currentUser == null || CartManager.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please log in and add at least one item.");
            return;
        }
        try {
            OrderDAO.OrderResult result = new OrderDAO().saveOrder(
                    Session.currentUser.getUserId(), CartManager.getCart());
            renderReceipt(result);
            CartManager.clear();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Order failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderReceipt(OrderDAO.OrderResult result) {
        receipt.setText("FOOD ORDERING SYSTEM\n\nORDER RECEIPT\n");
        receipt.append("Order ID: " + result.getOrderId() + "\n");
        receipt.append("Date: " + result.getOrderDate() + "\n");
        receipt.append("Customer: " + result.getCustomerName() + "\n\n");
        appendItems();
        receipt.append("SUBTOTAL:    Rs. " + result.getSubtotal() + "\n");
        receipt.append("DELIVERY FEE: Rs. " + result.getDeliveryFee() + "\n");
        receipt.append("DISCOUNT:     Rs. " + result.getDiscount() + "\n");
        receipt.append("TOTAL:        Rs. " + result.getTotal() + "\n");
        JOptionPane.showMessageDialog(this, "Combined order placed successfully.");
    }
}
