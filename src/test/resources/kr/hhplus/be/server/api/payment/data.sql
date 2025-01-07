TRUNCATE TABLE payments;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE stocks;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE point_histories;
TRUNCATE TABLE points;
TRUNCATE TABLE users;

INSERT INTO users (id, name, created_at, updated_at) VALUES
    (1, '이다은', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO points (id, user_id, balance, version, created_at, updated_at) VALUES
    (1, 1, 1000000, 0, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO point_histories (id, point_id, amount, type, created_at, updated_at) VALUES
    (1, 1, 90000, 'USE', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY));

INSERT INTO categories (id, name, created_at, updated_at) VALUES
    (1, '전자기기', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO products (id, name, description, category_id, price, status, created_at, updated_at) VALUES
    (1, '무선이어폰', '고음질 무선이어폰', 1, 100000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO stocks (id, product_id, quantity, created_at, updated_at) VALUES
    (1, 1, 50, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO orders (id, user_id, total_amount, status, receiver_name, receiver_phone, shipping_address, created_at, updated_at) VALUES
    (1, 1, 100000, 'PAYMENT_PENDING', '이다은', '010-3333-3333', '서울시 송파구', NOW(), NOW());

INSERT INTO order_items (id, order_id, product_id, product_name, price, quantity, created_at, updated_at) VALUES
    (1, 1, 1, '무선이어폰', 100000, 1, NOW(), NOW());

INSERT INTO payments (id, order_id, total_amount, discount_amount, final_amount, status, created_at, updated_at) VALUES
    (1, 1, 100000, 0, 100000, 'PENDING', NOW(), NOW());
