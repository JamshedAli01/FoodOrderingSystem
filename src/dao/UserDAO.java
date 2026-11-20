package dao;

import database.DBConnection;
import model.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;

public class UserDAO {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    public boolean registerUser(User user) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            String hash = hashPassword(user.getPassword(), salt);
            String sql = "INSERT INTO users(full_name,email,username,password_hash,salt) VALUES(?,?,?,?,?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, user.getFullName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getUsername());
                ps.setString(4, hash);
                ps.setBytes(5, salt);
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to register user. Email or username may already exist.", e);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to register user.", e);
        }
    }

    public User loginUser(String username, String password) {
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !MessageDigest.isEqual(
                            hashPassword(password, rs.getBytes("salt")).getBytes(),
                            rs.getString("password_hash").getBytes())) return null;
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setRole(rs.getString("role"));
                return user;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to log in. Check the database connection.", e);
        }
    }

    private static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            StringBuilder result = new StringBuilder();
            for (byte value : hash) result.append(String.format("%02x", value));
            return result.toString();
        } finally {
            spec.clearPassword();
        }
    }
}
