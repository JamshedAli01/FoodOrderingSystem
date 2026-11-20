package dao;

import database.DBConnection;
import model.Restaurant;
import java.sql.*;
import java.util.ArrayList;

public class RestaurantDAO {
    public ArrayList<Restaurant> getRestaurants() {
        ArrayList<Restaurant> list = new ArrayList<>();
        String sql = "SELECT r.restaurant_id, r.restaurant_name, r.location, r.cuisine, r.phone, " +
                "r.is_open, COUNT(DISTINCT m.menu_id) AS item_count, " +
                "COALESCE(v.average_rating, 0) AS average_rating " +
                "FROM restaurants r LEFT JOIN menu_items m ON m.restaurant_id = r.restaurant_id " +
                "LEFT JOIN v_restaurant_ratings v ON v.restaurant_id = r.restaurant_id " +
                "GROUP BY r.restaurant_id, r.restaurant_name, r.location, r.cuisine, r.phone, " +
                "r.is_open, v.average_rating ORDER BY r.restaurant_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Restaurant restaurant = new Restaurant(rs.getInt("restaurant_id"),
                        rs.getString("restaurant_name"), rs.getString("location"));
                restaurant.setCuisine(rs.getString("cuisine"));
                restaurant.setPhone(rs.getString("phone"));
                restaurant.setOpen(rs.getBoolean("is_open"));
                restaurant.setItemCount(rs.getInt("item_count"));
                restaurant.setAverageRating(rs.getDouble("average_rating"));
                list.add(restaurant);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load restaurants.", e);
        }
        return list;
    }
}
