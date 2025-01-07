TRUNCATE TABLE point_histories;
TRUNCATE TABLE points;
TRUNCATE TABLE users;

INSERT INTO users (id, name, created_at, updated_at) VALUES
    (1, '이다은', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO points (id, user_id, balance, version, created_at, updated_at) VALUES
    (1, 1, 1000000, 0, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO point_histories (id, point_id, amount, type, created_at, updated_at) VALUES
    (1, 1, 90000, 'USE', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY));