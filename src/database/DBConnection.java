package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class DBConnection {
    private DBConnection() {}

    public static Connection getConnection() throws Exception {
        Properties properties = new Properties();
        Path configPath = Paths.get("db.properties");
        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Database configuration is missing. Copy db.properties.example to db.properties.", e);
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        try {
            return DriverManager.getConnection(
                    required(properties, "db.url"),
                    required(properties, "db.user"),
                    properties.getProperty("db.password", ""));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to connect to MySQL. Check db.properties and ensure MySQL is running.", e);
        }
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing database property: " + key);
        }
        return value;
    }
}
