USE food_ordering_system;

-- 1. INNER JOIN: show each menu item and its restaurant.
SELECT r.restaurant_name, m.item_name, m.price
FROM restaurants r INNER JOIN menu_items m ON m.restaurant_id = r.restaurant_id;

-- 2. LEFT JOIN: include restaurants even when a menu is empty.
SELECT r.restaurant_name, COUNT(m.menu_id) AS item_count
FROM restaurants r LEFT JOIN menu_items m ON m.restaurant_id = r.restaurant_id
GROUP BY r.restaurant_id, r.restaurant_name;

-- 3. GROUP BY and HAVING: restaurants with at least 15 items.
SELECT r.restaurant_name, COUNT(m.menu_id) AS item_count
FROM restaurants r JOIN menu_items m ON m.restaurant_id = r.restaurant_id
GROUP BY r.restaurant_id, r.restaurant_name
HAVING COUNT(m.menu_id) >= 15;

-- 4. Subquery: items priced above the overall average.
SELECT item_name, price
FROM menu_items
WHERE price > (SELECT AVG(price) FROM menu_items);

-- 5. Aggregate functions.
SELECT COUNT(*) AS item_count, MIN(price) AS cheapest, MAX(price) AS highest,
       AVG(price) AS average_price
FROM menu_items;

-- 6. Search and ordering.
SELECT item_name, price FROM menu_items
WHERE item_name LIKE '%Chicken%'
ORDER BY price DESC LIMIT 10;

-- 7. Order summary view for combined multi-restaurant orders.
SELECT * FROM v_order_summary ORDER BY order_date DESC;

-- 8. Top-selling-items view.
SELECT * FROM v_top_selling_items
ORDER BY units_sold DESC, revenue DESC LIMIT 10;

-- 9. Restaurant sales view.
SELECT * FROM v_restaurant_sales ORDER BY revenue DESC;

-- 10. Restaurant ratings view.
SELECT * FROM v_restaurant_ratings
WHERE review_count > 0 ORDER BY average_rating DESC;

-- 11. Stored procedure report.
CALL sp_sales_report(CURDATE() - INTERVAL 30 DAY, CURDATE());

-- 12. Transaction example: the test order is rolled back.
START TRANSACTION;
INSERT INTO orders(user_id, delivery_address, subtotal, delivery_fee, total_amount)
VALUES (1, 'Rollback test address', 100.00, 100.00, 200.00);
ROLLBACK;

-- 13. Constraint violation example: CHECK(price > 0) rejects this row.
INSERT INTO menu_items(restaurant_id, category_id, item_name, price)
VALUES (1, 1, 'Invalid Negative Price', -1.00);
