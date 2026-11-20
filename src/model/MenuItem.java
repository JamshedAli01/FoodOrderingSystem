package model;

import java.math.BigDecimal;

public class MenuItem {
    private int itemId;
    private int restaurantId;
    private int categoryId;
    private String itemName;
    private String description;
    private BigDecimal price;
    private boolean available;

    public MenuItem() {}

    public MenuItem(int itemId, int restaurantId, String itemName, BigDecimal price) {
        this.itemId = itemId;
        this.restaurantId = restaurantId;
        this.itemName = itemName;
        this.price = price;
    }

    public int getItemId() { return itemId; }
    public int getRestaurantId() { return restaurantId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
