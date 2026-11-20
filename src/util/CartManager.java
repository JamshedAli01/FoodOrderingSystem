package util;

import model.CartItem;
import model.Restaurant;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CartManager {
    private static final ArrayList<CartItem> cart = new ArrayList<>();
    private static Restaurant lastRestaurant;

    private CartManager() {}

    public static void addItem(int menuId, int restaurantId, String restaurantName,
                               String itemName, BigDecimal price, int qty) {
        for (CartItem item : cart) {
            if (item.getMenuId() == menuId) {
                item.setQuantity(item.getQuantity() + qty);
                return;
            }
        }
        cart.add(new CartItem(menuId, restaurantId, restaurantName, itemName, price, qty));
    }

    public static void setLastRestaurant(Restaurant restaurant) {
        lastRestaurant = restaurant;
    }

    public static Restaurant getLastRestaurant() {
        return lastRestaurant;
    }

    public static ArrayList<CartItem> getCart() { return cart; }

    public static BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) total = total.add(item.getTotal());
        return total;
    }

    public static Map<String, List<CartItem>> groupByRestaurant() {
        Map<String, List<CartItem>> groups = new LinkedHashMap<>();
        for (CartItem item : cart) {
            String name = item.getRestaurantName() == null
                    ? "Restaurant #" + item.getRestaurantId() : item.getRestaurantName();
            groups.computeIfAbsent(name, ignored -> new ArrayList<>()).add(item);
        }
        return groups;
    }

    public static BigDecimal getRestaurantTotal(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) total = total.add(item.getTotal());
        return total;
    }

    public static void removeItem(int menuId) {
        cart.removeIf(item -> item.getMenuId() == menuId);
    }

    public static void clear() { cart.clear(); }
}
