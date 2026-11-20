package dao;

import database.DBConnection;
import model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenuDAO {
    public ArrayList<MenuItem> getMenu(int restaurantId) {
        ArrayList<MenuItem> menu = new ArrayList<>();
        String sql = "SELECT menu_id, restaurant_id, category_id, item_name, description, price, is_available " +
                "FROM menu_items WHERE restaurant_id = ? ORDER BY item_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(rs.getInt("menu_id"), rs.getInt("restaurant_id"),
                            rs.getString("item_name"), rs.getBigDecimal("price"));
                    item.setCategoryId(rs.getInt("category_id"));
                    item.setDescription(rs.getString("description"));
                    item.setAvailable(rs.getBoolean("is_available"));
                    menu.add(item);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load menu items.", e);
        }
        return menu;
    }

    public Map<Integer, String> getCategories() {
        Map<Integer, String> categories = new LinkedHashMap<>();
        String sql = "SELECT category_id, category_name FROM categories ORDER BY category_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.put(rs.getInt("category_id"), rs.getString("category_name"));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load menu categories.", e);
        }
        return categories;
    }
}
