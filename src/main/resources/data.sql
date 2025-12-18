-- Initial data BRANDS
INSERT INTO BRANDS (id, chain_name) VALUES (1, 'ZARA');

-- Initial data PRODUCTS
INSERT INTO PRODUCTS (id, product_name) VALUES (35455, 'Product 35455');

-- Initial data PRICES
INSERT INTO PRICES (price_list, brand_id, product_id, start_date, end_date, priority, price, currency) VALUES
(1, 1, 35455, '2020-06-14 00:00:00', '2020-12-31 23:59:59', 0, 35.50, 'EUR'),
(2, 1, 35455, '2020-06-14 15:00:00', '2020-06-14 18:30:00', 1, 25.45, 'EUR'),
(3, 1, 35455, '2020-06-15 00:00:00', '2020-06-15 11:00:00', 1, 30.50, 'EUR'),
(4, 1, 35455, '2020-06-15 16:00:00', '2020-12-31 23:59:59', 1, 38.95, 'EUR');

