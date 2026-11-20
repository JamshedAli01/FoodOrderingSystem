package model;

public class Restaurant {

    private int restaurantId;
    private String restaurantName;
    private String location;
    private String cuisine;
    private String phone;
    private boolean open;
    private int itemCount;
    private double averageRating;

    public Restaurant(){}

    public Restaurant(int restaurantId,String restaurantName,String location){

        this.restaurantId=restaurantId;
        this.restaurantName=restaurantName;
        this.location=location;

    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getLocation() {
        return location;
    }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

}