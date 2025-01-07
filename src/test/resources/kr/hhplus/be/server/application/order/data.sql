TRUNCATE TABLE stocks;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE point_histories;
TRUNCATE TABLE points;
TRUNCATE TABLE users;

INSERT INTO users (id, name, created_at, updated_at) VALUES
    (1, '이다은', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (2, '황지수', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (3, '이호민', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO points (id, user_id, balance, version, created_at, updated_at) VALUES
    (1, 1, 1000000, 0, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (2, 2, 1000000, 0, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (3, 3, 1000000, 0, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO categories (id, name, created_at, updated_at) VALUES
    (1, '전자기기', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO products (id, name, description, category_id, price, status, created_at, updated_at) VALUES
    (1, '무선이어폰', '고음질 무선이어폰', 1, 100000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO stocks (id, product_id, quantity, created_at, updated_at) VALUES
    (1, 1, 2, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));
