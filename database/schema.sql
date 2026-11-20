CREATE DATABASE IF NOT EXISTS food_ordering_system;
USE food_ordering_system;

-- DBMS concept: dropping in dependency order makes this source reproducible.
SET FOREIGN_KEY_CHECKS = 0;
DROP VIEW IF EXISTS v_order_summary, v_top_selling_items, v_restaurant_sales, v_restaurant_ratings;
DROP PROCEDURE IF EXISTS sp_sales_report;
DROP TABLE IF EXISTS order_status_log, reviews, order_items, orders, promo_codes,
    menu_items, categories, restaurants, users;
SET FOREIGN_KEY_CHECKS = 1;

-- DBMS concept: each entity has one stable primary key and attributes are atomic (3NF).
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255),
    password_hash CHAR(64) NOT NULL,
    salt VARBINARY(32) NOT NULL,
    role ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE restaurants (
    restaurant_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_name VARCHAR(120) NOT NULL UNIQUE,
    location VARCHAR(120) NOT NULL,
    cuisine VARCHAR(100),
    phone VARCHAR(20),
    opening_time TIME,
    closing_time TIME,
    is_open BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(60) NOT NULL UNIQUE
);

-- DBMS concept: restaurant/category foreign keys prevent orphan menu rows.
CREATE TABLE menu_items (
    menu_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    category_id INT NOT NULL,
    item_name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (restaurant_id, item_name),
    CONSTRAINT fk_menu_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE INDEX idx_menu_item_name ON menu_items(item_name);
CREATE INDEX idx_menu_restaurant ON menu_items(restaurant_id);

CREATE TABLE promo_codes (
    promo_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    discount_percent DECIMAL(5,2) NOT NULL CHECK (discount_percent BETWEEN 0 AND 100),
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (valid_to >= valid_from)
);

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PLACED','PREPARING','OUT_FOR_DELIVERY','DELIVERED','CANCELLED')
        NOT NULL DEFAULT 'PLACED',
    payment_method ENUM('CASH_ON_DELIVERY','CARD','EASYPAISA','JAZZCASH')
        NOT NULL DEFAULT 'CASH_ON_DELIVERY',
    delivery_address VARCHAR(255) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (delivery_fee >= 0),
    discount DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (discount >= 0),
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    promo_id INT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_order_promo FOREIGN KEY (promo_id) REFERENCES promo_codes(promo_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_date ON orders(order_date);

-- DBMS concept: unit_price is a historical snapshot; line_total is derived by a trigger.
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price > 0),
    line_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    UNIQUE (order_id, menu_id),
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_item_menu FOREIGN KEY (menu_id) REFERENCES menu_items(menu_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    order_id INT NOT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (order_id, restaurant_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id) ON DELETE RESTRICT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

CREATE TABLE order_status_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

DELIMITER //
CREATE TRIGGER trg_order_items_line_total
BEFORE INSERT ON order_items
FOR EACH ROW SET NEW.line_total = NEW.quantity * NEW.unit_price//

CREATE TRIGGER trg_order_status_log
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NOT (OLD.status <=> NEW.status) THEN
        INSERT INTO order_status_log(order_id, old_status, new_status)
        VALUES (NEW.order_id, OLD.status, NEW.status);
    END IF;
END//

CREATE TRIGGER trg_review_delivered
BEFORE INSERT ON reviews
FOR EACH ROW
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM orders o
        WHERE o.order_id = NEW.order_id
          AND o.user_id = NEW.user_id
          AND o.status = 'DELIVERED'
          AND EXISTS (
              SELECT 1
              FROM order_items oi
              JOIN menu_items mi ON mi.menu_id = oi.menu_id
              WHERE oi.order_id = o.order_id
                AND mi.restaurant_id = NEW.restaurant_id
          )
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Reviews require a delivered order';
    END IF;
END//
DELIMITER ;

-- DBMS concept: views centralize reusable joins and reporting logic.
CREATE VIEW v_order_summary AS
SELECT o.order_id, o.order_date, o.status, o.payment_method, u.user_id,
       u.full_name, u.email,
       GROUP_CONCAT(DISTINCT r.restaurant_name ORDER BY r.restaurant_name SEPARATOR ', ') AS restaurant_names,
       o.subtotal, o.delivery_fee, o.discount, o.total_amount
FROM orders o JOIN users u ON u.user_id = o.user_id
JOIN order_items oi ON oi.order_id = o.order_id
JOIN menu_items mi ON mi.menu_id = oi.menu_id
JOIN restaurants r ON r.restaurant_id = mi.restaurant_id
GROUP BY o.order_id, o.order_date, o.status, o.payment_method, u.user_id,
         u.full_name, u.email, o.subtotal, o.delivery_fee, o.discount, o.total_amount;

CREATE VIEW v_top_selling_items AS
SELECT m.menu_id, m.item_name, r.restaurant_name, SUM(oi.quantity) AS units_sold,
       SUM(oi.line_total) AS revenue
FROM order_items oi JOIN menu_items m ON m.menu_id = oi.menu_id
JOIN restaurants r ON r.restaurant_id = m.restaurant_id
JOIN orders o ON o.order_id = oi.order_id
WHERE o.status <> 'CANCELLED'
GROUP BY m.menu_id, m.item_name, r.restaurant_name;

CREATE VIEW v_restaurant_sales AS
SELECT r.restaurant_id, r.restaurant_name,
       COUNT(DISTINCT o.order_id) AS order_count,
       COALESCE(SUM(oi.line_total), 0) AS revenue
FROM restaurants r
LEFT JOIN menu_items mi ON mi.restaurant_id = r.restaurant_id
LEFT JOIN order_items oi ON oi.menu_id = mi.menu_id
LEFT JOIN orders o ON o.order_id = oi.order_id AND o.status <> 'CANCELLED'
GROUP BY r.restaurant_id, r.restaurant_name;

CREATE VIEW v_restaurant_ratings AS
SELECT r.restaurant_id, r.restaurant_name, COUNT(rv.review_id) AS review_count,
       COALESCE(AVG(rv.rating), 0) AS average_rating
FROM restaurants r LEFT JOIN reviews rv ON rv.restaurant_id = r.restaurant_id
GROUP BY r.restaurant_id, r.restaurant_name;

DELIMITER //
CREATE PROCEDURE sp_sales_report(IN from_date DATE, IN to_date DATE)
BEGIN
    SELECT r.restaurant_id, r.restaurant_name,
           COUNT(DISTINCT o.order_id) AS order_count,
           COALESCE(SUM(oi.line_total), 0) AS revenue
    FROM restaurants r
    LEFT JOIN menu_items mi ON mi.restaurant_id = r.restaurant_id
    LEFT JOIN order_items oi ON oi.menu_id = mi.menu_id
    LEFT JOIN orders o ON o.order_id = oi.order_id
      AND o.order_date >= from_date
      AND o.order_date < DATE_ADD(to_date, INTERVAL 1 DAY)
      AND o.status <> 'CANCELLED'
    GROUP BY r.restaurant_id, r.restaurant_name
    ORDER BY revenue DESC;
END//
DELIMITER ;
