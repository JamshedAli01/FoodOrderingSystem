USE food_ordering_system;

-- DBMS concept: unique names plus INSERT IGNORE make seed data safely re-runnable.
INSERT IGNORE INTO categories(category_name) VALUES
('Pizza'),('Rolls'),('BBQ'),('Karahi'),('Rice'),('Breads'),('Drinks'),
('Desserts'),('Breakfast'),('Sajji'),('Sindhi');

INSERT IGNORE INTO restaurants(restaurant_name, location, cuisine, is_open) VALUES
('Al-Asr Foods','Tando Adam','Fast Food',TRUE),
('Khan Baba Restaurant','Tando Adam','BBQ and Pakistani',TRUE),
('Indus Restaurant','Tando Adam','Sindhi and Pakistani',TRUE),
('Brohi Balochistan Restaurant','Tando Adam','Balochi',TRUE),
('Geo Mari Restaurant','Tando Adam','Pakistani',TRUE),
('Dua Salateen Restaurant','Tando Adam','Breakfast and Pakistani',TRUE),
('Dahiri Hotel','Tando Adam','Pakistani',TRUE),
('Tando Adam Siji House','Tando Adam','Sajji',TRUE);

INSERT IGNORE INTO promo_codes(code, discount_percent, valid_from, valid_to, is_active) VALUES
('WELCOME10',10,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 1 YEAR),TRUE),
('STUDENT15',15,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 1 YEAR),TRUE);

-- DBMS concept: restaurant and category names are looked up instead of hardcoded IDs.
-- Every restaurant has 15 menu items; Al-Asr retains all 15 original items and prices.
INSERT IGNORE INTO menu_items(restaurant_id, category_id, item_name, description, price)
SELECT r.restaurant_id, c.category_id, x.item_name, x.description, x.price
FROM (
    SELECT 'Al-Asr Foods' restaurant_name,'Rolls' category_name,'Kabab Boti Roll' item_name,'Kabab boti roll' description,300.00 price
    UNION ALL SELECT 'Al-Asr Foods','Rolls','Chicken Roll','Chicken roll',100
    UNION ALL SELECT 'Al-Asr Foods','Rolls','Malai Boti','Creamy grilled chicken',250
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Legend Ranch Pizza (Mini)','Mini ranch pizza',900
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Legend Ranch Pizza (Large)','Large ranch pizza',1200
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Pizza Fajita (Mini)','Mini fajita pizza',900
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Pizza Fajita (Large)','Large fajita pizza',1200
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Margherita Pizza (Mini)','Mini margherita pizza',900
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Margherita Pizza (Large)','Large margherita pizza',1200
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Chicken Tikka Pizza (Mini)','Mini tikka pizza',900
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Chicken Tikka Pizza (Large)','Large tikka pizza',1300
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Cheese Overloaded Pizza (Mini)','Mini cheese pizza',900
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Cheese Overloaded Pizza (Large)','Large cheese pizza',1200
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Veggie Overloaded Pizza (Mini)','Mini vegetable pizza',900
    UNION ALL SELECT 'Al-Asr Foods','Pizza','Veggie Overloaded Pizza (Large)','Large vegetable pizza',1200
    UNION ALL SELECT 'Al-Asr Foods','BBQ','Chicken Wings','Crispy chicken wings',450
    UNION ALL SELECT 'Al-Asr Foods','Drinks','Mint Margarita','Chilled mint drink',180
    UNION ALL SELECT 'Al-Asr Foods','Drinks','Cold Drink','Chilled soft drink',100
    UNION ALL SELECT 'Al-Asr Foods','Desserts','Chocolate Lava Cake','Warm chocolate cake',300
    UNION ALL SELECT 'Al-Asr Foods','Breads','Garlic Bread','Toasted garlic bread',250

    UNION ALL SELECT 'Khan Baba Restaurant','BBQ','Chicken Tikka','Grilled chicken tikka',500
    UNION ALL SELECT 'Khan Baba Restaurant','BBQ','Mutton Tikka','Grilled mutton tikka',750
    UNION ALL SELECT 'Khan Baba Restaurant','BBQ','Seekh Kabab','Beef seekh kabab',450
    UNION ALL SELECT 'Khan Baba Restaurant','BBQ','Chicken Boti','Chicken boti',550
    UNION ALL SELECT 'Khan Baba Restaurant','BBQ','Malai Boti','Creamy chicken boti',600
    UNION ALL SELECT 'Khan Baba Restaurant','Karahi','Chicken Karahi','Traditional chicken karahi',1200
    UNION ALL SELECT 'Khan Baba Restaurant','Karahi','Mutton Karahi','Traditional mutton karahi',1800
    UNION ALL SELECT 'Khan Baba Restaurant','Karahi','Daal Mash','Slow cooked lentils',300
    UNION ALL SELECT 'Khan Baba Restaurant','Rice','Chicken Biryani','Chicken biryani',400
    UNION ALL SELECT 'Khan Baba Restaurant','Rice','Mutton Pulao','Mutton pulao',550
    UNION ALL SELECT 'Khan Baba Restaurant','Breads','Naan','Tandoori naan',50
    UNION ALL SELECT 'Khan Baba Restaurant','Breads','Garlic Naan','Garlic naan',100
    UNION ALL SELECT 'Khan Baba Restaurant','Breads','Roghni Naan','Sesame naan',100
    UNION ALL SELECT 'Khan Baba Restaurant','Drinks','Lassi','Fresh salted lassi',180
    UNION ALL SELECT 'Khan Baba Restaurant','Desserts','Kheer','Rice pudding',180

    UNION ALL SELECT 'Indus Restaurant','Sindhi','Sindhi Biryani','Sindhi rice dish',350
    UNION ALL SELECT 'Indus Restaurant','Rice','Chicken Biryani','Chicken biryani',400
    UNION ALL SELECT 'Indus Restaurant','Rice','Mutton Biryani','Mutton biryani',550
    UNION ALL SELECT 'Indus Restaurant','Breakfast','Daal Chawal','Lentils and rice',250
    UNION ALL SELECT 'Indus Restaurant','Sindhi','Sai Bhaji','Sindhi spinach curry',300
    UNION ALL SELECT 'Indus Restaurant','Sindhi','Bhindi Masala','Okra masala',280
    UNION ALL SELECT 'Indus Restaurant','Karahi','Chicken Karahi','Chicken karahi',1100
    UNION ALL SELECT 'Indus Restaurant','Karahi','Mutton Karahi','Mutton karahi',1700
    UNION ALL SELECT 'Indus Restaurant','BBQ','Chicken Tikka','Grilled chicken tikka',500
    UNION ALL SELECT 'Indus Restaurant','BBQ','Seekh Kabab','Beef seekh kabab',450
    UNION ALL SELECT 'Indus Restaurant','Breads','Naan','Tandoori naan',50
    UNION ALL SELECT 'Indus Restaurant','Breads','Chapati','Whole wheat chapati',30
    UNION ALL SELECT 'Indus Restaurant','Drinks','Lassi','Fresh lassi',180
    UNION ALL SELECT 'Indus Restaurant','Desserts','Kheer','Rice pudding',180
    UNION ALL SELECT 'Indus Restaurant','Desserts','Gulab Jamun','Sweet dumplings',150

    UNION ALL SELECT 'Brohi Balochistan Restaurant','Sajji','Chicken Sajji','Balochi chicken sajji',1800
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Sajji','Mutton Sajji','Balochi mutton sajji',3200
    UNION ALL SELECT 'Brohi Balochistan Restaurant','BBQ','Balochi Rosh','Slow cooked meat',1500
    UNION ALL SELECT 'Brohi Balochistan Restaurant','BBQ','Balochi Tikka','Balochi grilled tikka',700
    UNION ALL SELECT 'Brohi Balochistan Restaurant','BBQ','Seekh Kabab','Beef seekh kabab',450
    UNION ALL SELECT 'Brohi Balochistan Restaurant','BBQ','Chapli Kabab','Spiced minced kabab',450
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Karahi','Mutton Karahi','Mutton karahi',1800
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Karahi','Chicken Karahi','Chicken karahi',1200
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Rice','Mutton Pulao','Mutton pulao',550
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Rice','Chicken Pulao','Chicken pulao',400
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Breads','Naan','Tandoori naan',50
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Breads','Roghni Naan','Sesame naan',100
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Drinks','Lassi','Fresh lassi',180
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Drinks','Cold Drink','Chilled soft drink',100
    UNION ALL SELECT 'Brohi Balochistan Restaurant','Desserts','Kheer','Rice pudding',180

    UNION ALL SELECT 'Geo Mari Restaurant','Karahi','Mutton Karahi','Mutton karahi',1600
    UNION ALL SELECT 'Geo Mari Restaurant','Karahi','Chicken Karahi','Chicken karahi',1100
    UNION ALL SELECT 'Geo Mari Restaurant','Rice','Mutton Biryani','Mutton biryani',550
    UNION ALL SELECT 'Geo Mari Restaurant','Rice','Chicken Biryani','Chicken biryani',400
    UNION ALL SELECT 'Geo Mari Restaurant','Rice','Chicken Pulao','Chicken pulao',350
    UNION ALL SELECT 'Geo Mari Restaurant','BBQ','Chicken Tikka','Grilled chicken tikka',500
    UNION ALL SELECT 'Geo Mari Restaurant','BBQ','Malai Boti','Creamy chicken boti',600
    UNION ALL SELECT 'Geo Mari Restaurant','BBQ','Seekh Kabab','Beef seekh kabab',450
    UNION ALL SELECT 'Geo Mari Restaurant','Sindhi','Daal Chawal','Lentils and rice',250
    UNION ALL SELECT 'Geo Mari Restaurant','Sindhi','Mix Sabzi','Seasonal vegetables',280
    UNION ALL SELECT 'Geo Mari Restaurant','Breads','Naan','Tandoori naan',50
    UNION ALL SELECT 'Geo Mari Restaurant','Breads','Roti','Fresh roti',30
    UNION ALL SELECT 'Geo Mari Restaurant','Drinks','Lassi','Fresh lassi',180
    UNION ALL SELECT 'Geo Mari Restaurant','Desserts','Kheer','Rice pudding',180
    UNION ALL SELECT 'Geo Mari Restaurant','Desserts','Gulab Jamun','Sweet dumplings',150

    UNION ALL SELECT 'Dua Salateen Restaurant','Breakfast','Halwa Puri','Breakfast platter',250
    UNION ALL SELECT 'Dua Salateen Restaurant','Breakfast','Chana Paratha','Chickpeas and paratha',220
    UNION ALL SELECT 'Dua Salateen Restaurant','Breakfast','Nihari','Slow cooked beef stew',450
    UNION ALL SELECT 'Dua Salateen Restaurant','Breakfast','Aloo Paratha','Potato stuffed paratha',180
    UNION ALL SELECT 'Dua Salateen Restaurant','Breakfast','Omelette Paratha','Egg omelette with paratha',200
    UNION ALL SELECT 'Dua Salateen Restaurant','Breakfast','Chai Paratha','Tea with paratha',180
    UNION ALL SELECT 'Dua Salateen Restaurant','Karahi','Chicken Karahi','Chicken karahi',1100
    UNION ALL SELECT 'Dua Salateen Restaurant','BBQ','Chicken Tikka','Grilled chicken tikka',500
    UNION ALL SELECT 'Dua Salateen Restaurant','BBQ','Seekh Kabab','Beef seekh kabab',450
    UNION ALL SELECT 'Dua Salateen Restaurant','Rice','Chicken Biryani','Chicken biryani',400
    UNION ALL SELECT 'Dua Salateen Restaurant','Breads','Naan','Tandoori naan',50
    UNION ALL SELECT 'Dua Salateen Restaurant','Breads','Roti','Fresh roti',30
    UNION ALL SELECT 'Dua Salateen Restaurant','Drinks','Chai','Pakistani tea',100
    UNION ALL SELECT 'Dua Salateen Restaurant','Drinks','Lassi','Fresh lassi',180
    UNION ALL SELECT 'Dua Salateen Restaurant','Desserts','Kheer','Rice pudding',180

    UNION ALL SELECT 'Dahiri Hotel','Karahi','Dahiri Chicken Karahi','House chicken karahi',1100
    UNION ALL SELECT 'Dahiri Hotel','Karahi','Mutton Karahi','Mutton karahi',1700
    UNION ALL SELECT 'Dahiri Hotel','Rice','Chicken Pulao','Pulao rice',350
    UNION ALL SELECT 'Dahiri Hotel','Rice','Mutton Biryani','Mutton biryani',550
    UNION ALL SELECT 'Dahiri Hotel','BBQ','Chicken Tikka','Grilled chicken tikka',500
    UNION ALL SELECT 'Dahiri Hotel','BBQ','Chicken Boti','Chicken boti',550
    UNION ALL SELECT 'Dahiri Hotel','BBQ','Seekh Kabab','Beef seekh kabab',450
    UNION ALL SELECT 'Dahiri Hotel','Sindhi','Daal Chawal','Lentils and rice',250
    UNION ALL SELECT 'Dahiri Hotel','Sindhi','Aloo Gobi','Potato and cauliflower',280
    UNION ALL SELECT 'Dahiri Hotel','Breads','Naan','Tandoori naan',50
    UNION ALL SELECT 'Dahiri Hotel','Breads','Roti','Fresh roti',30
    UNION ALL SELECT 'Dahiri Hotel','Drinks','Lassi','Fresh lassi',180
    UNION ALL SELECT 'Dahiri Hotel','Drinks','Cold Drink','Chilled soft drink',100
    UNION ALL SELECT 'Dahiri Hotel','Desserts','Kheer','Rice pudding',180
    UNION ALL SELECT 'Dahiri Hotel','Desserts','Gulab Jamun','Sweet dumplings',150

    UNION ALL SELECT 'Tando Adam Siji House','Sajji','Bari Siji','Large whole sajji',4500
    UNION ALL SELECT 'Tando Adam Siji House','Sajji','Chhoti Siji','Small whole sajji',3500
    UNION ALL SELECT 'Tando Adam Siji House','Breads','Naan','Fresh naan',50
    UNION ALL SELECT 'Tando Adam Siji House','Breads','Roti','Fresh roti',30
    UNION ALL SELECT 'Tando Adam Siji House','Drinks','Cold Drink','Chilled soft drink',100
    UNION ALL SELECT 'Tando Adam Siji House','Drinks','Chai','Tea',100
    UNION ALL SELECT 'Tando Adam Siji House','Rice','Plain Rice','Steamed rice',250
    UNION ALL SELECT 'Tando Adam Siji House','Rice','Chicken Pulao','Chicken pulao',400
    UNION ALL SELECT 'Tando Adam Siji House','BBQ','Chicken Tikka','Grilled chicken tikka',500
    UNION ALL SELECT 'Tando Adam Siji House','BBQ','Seekh Kabab','Beef seekh kabab',450
    UNION ALL SELECT 'Tando Adam Siji House','Desserts','Raita','Yogurt raita',100
    UNION ALL SELECT 'Tando Adam Siji House','Desserts','Salad','Fresh salad',120
    UNION ALL SELECT 'Tando Adam Siji House','Desserts','Kheer','Rice pudding',180
    UNION ALL SELECT 'Tando Adam Siji House','BBQ','Mutton Tikka','Grilled mutton tikka',750
    UNION ALL SELECT 'Tando Adam Siji House','Breads','Garlic Naan','Garlic naan',100
) x
JOIN restaurants r ON r.restaurant_name = x.restaurant_name
JOIN categories c ON c.category_name = x.category_name;
