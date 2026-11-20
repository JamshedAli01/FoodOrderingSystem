package model;

import java.math.BigDecimal;

public class CartItem {
    private int menuId;
    private int restaurantId;
    private String restaurantName;
    private String itemName;
    private BigDecimal price;
    private int quantity;

    public CartItem() {}

    public CartItem(int menuId, int restaurantId, String itemName, BigDecimal price, int quantity) {
        this.menuId = menuId;
        this.restaurantId = restaurantId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    public CartItem(int menuId, int restaurantId, String restaurantName,
                    String itemName, BigDecimal price, int quantity) {
        this(menuId, restaurantId, itemName, price, quantity);
        this.restaurantName = restaurantName;
    }

    public int getMenuId() { return menuId; }
    public int getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public String getItemName() { return itemName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getTotal() { return price.multiply(BigDecimal.valueOf(quantity)); }
}
