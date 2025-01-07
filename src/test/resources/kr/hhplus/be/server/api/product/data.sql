TRUNCATE TABLE payments;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE stocks;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;

INSERT INTO users (id, name, created_at, updated_at) VALUES
    (1, '이다은', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO categories (id, name, created_at, updated_at) VALUES
    (1, '전자기기', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (2, '의류', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (3, '식품', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (4, '도서', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO products (id, name, description, category_id, price, status, created_at, updated_at) VALUES
    (1, '무선이어폰', '고음질 무선이어폰', 1, 100000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (2, '태블릿PC', '가성비 태블릿', 1, 200000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (3, '스마트폰', '최신형 스마트폰', 1, 1200000, 'SUSPENDED', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (4, '노트북', '고성능 노트북', 1, 1500000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (5, '스마트워치', '건강관리 스마트워치', 1, 300000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (6, '블루투스 스피커', '고음질 휴대용 스피커', 1, 150000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (7, '게이밍 마우스', 'RGB 게이밍 마우스', 1, 80000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (8, '기계식 키보드', '청축 기계식 키보드', 1, 120000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (9, '외장 SSD', '1TB 외장 SSD', 1, 180000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (10, '웹캠', '풀HD 웹캠', 1, 90000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (11, '그래픽카드', '게이밍용 그래픽카드', 1, 800000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (12, '공유기', '와이파이6 공유기', 1, 250000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (13, '모니터', '32인치 게이밍 모니터', 1, 450000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (14, 'CPU', '최신형 프로세서', 1, 550000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (15, '메인보드', 'ATX 메인보드', 1, 280000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (16, '파워서플라이', '80플러스 골드 파워', 1, 160000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (17, '케이스', '미들타워 케이스', 1, 130000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (18, 'RAM', '32GB DDR4 RAM', 1, 220000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (19, '프린터', '컬러 레이저 프린터', 1, 350000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (20, 'UPS', '무정전 전원공급장치', 1, 400000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (21, '겨울코트', '따뜻한 겨울코트', 2, 150000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (22, '니트스웨터', '부드러운 니트스웨터', 2, 80000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (23, '유기농식품세트', '신선한 유기농 식품', 3, 50000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (24, '프리미엄과일세트', '제철 과일 모음', 3, 70000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (25, '베스트셀러책', '인기 도서', 4, 20000, 'ON_SALE', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO stocks (id, product_id, quantity, created_at, updated_at) VALUES
    (1, 1, 50, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (2, 2, 30, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (3, 3, 100, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (4, 4, 150, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (5, 5, 80, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (6, 6, 60, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (7, 7, 200, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (8, 8, 50, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (9, 9, 30, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (10, 10, 100, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (11, 11, 150, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (12, 12, 80, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (13, 13, 60, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (14, 14, 200, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (15, 15, 50, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (16, 16, 30, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (17, 17, 100, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (18, 18, 150, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (19, 19, 150, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (20, 20, 150, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (21, 21, 150, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (22, 22, 150, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (23, 23, 80, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (24, 24, 60, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (25, 25, 200, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

INSERT INTO orders (id, user_id, total_amount, status, receiver_name, receiver_phone, shipping_address, created_at, updated_at) VALUES
    -- 7일 전
    (1, 1, 100000, 'COMPLETED', '김철수', '010-1111-1111', '서울시 강남구', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (2, 1, 50000, 'COMPLETED', '이영희', '010-2222-2222', '서울시 서초구', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    -- 6일 전
    (3, 1, 200000, 'COMPLETED', '박지민', '010-3333-3333', '서울시 송파구', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    -- 5일 전
    (4, 1, 50000, 'COMPLETED', '정민수', '010-4444-4444', '서울시 마포구', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    -- 3일 전
    (5, 1, 200000, 'COMPLETED', '김철수', '010-1111-1111', '서울시 강남구', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (6, 1, 230000, 'COMPLETED', '이영희', '010-2222-2222', '서울시 서초구', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (7, 1, 120000, 'COMPLETED', '박지민', '010-3333-3333', '서울시 송파구', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    -- 2일 전
    (8, 1, 400000, 'COMPLETED', '정민수', '010-4444-4444', '서울시 마포구', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (9, 1, 150000, 'COMPLETED', '강다혜', '010-5555-5555', '서울시 강동구', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (10, 1, 150000, 'COMPLETED', '김철수', '010-1111-1111', '서울시 강남구', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    -- 1일 전
    (11, 1, 300000, 'COMPLETED', '이영희', '010-2222-2222', '서울시 서초구', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (12, 1, 150000, 'COMPLETED', '박지민', '010-3333-3333', '서울시 송파구', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (13, 1, 120000, 'COMPLETED', '정민수', '010-4444-4444', '서울시 마포구', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    -- 현재
    (14, 1, 350000, 'PAYMENT_PENDING', '박지민', '010-3333-3333', '서울시 송파구', NOW(), NOW()),
    (15, 1, 130000, 'PAYMENT_PENDING', '박지민', '010-3333-3333', '서울시 송파구', NOW(), NOW());

INSERT INTO order_items (id, order_id, product_id, product_name, price, quantity, created_at, updated_at) VALUES
-- 7일 전 주문
    (1, 1, 1, '무선이어폰', 100000, 1, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (2, 2, 23, '유기농식품세트', 50000, 1, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    -- 6일 전 주문
    (3, 3, 2, '태블릿PC', 200000, 1, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    -- 5일 전 주문
    (4, 4, 23, '유기농식품세트', 50000, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    -- 3일 전 주문
    (5, 5, 2, '태블릿PC', 200000, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (6, 6, 21, '겨울코트', 150000, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (7, 6, 22, '니트스웨터', 80000, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (8, 7, 24, '프리미엄과일세트', 70000, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (9, 7, 23, '유기농식품세트', 50000, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    -- 2일 전 주문
    (10, 8, 1, '무선이어폰', 100000, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (11, 8, 5, '스마트워치', 300000, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (12, 9, 21, '겨울코트', 150000, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (13, 10, 22, '니트스웨터', 80000, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (14, 10, 24, '프리미엄과일세트', 70000, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    -- 1일 전 주문
    (15, 11, 2, '태블릿PC', 200000, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (16, 11, 1, '무선이어폰', 100000, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (17, 12, 21, '겨울코트', 150000, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (18, 13, 24, '프리미엄과일세트', 70000, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (19, 13, 23, '유기농식품세트', 50000, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    -- 현재
    (20, 14, 19, '프린터', 350000, 1, NOW(), NOW()),
    (21, 15, 17, '케이스', 130000, 1, NOW(), NOW());

INSERT INTO payments (id, order_id, total_amount, discount_amount, final_amount, status, created_at, updated_at) VALUES
    -- 7일 전 결제
    (1, 1, 100000, 10000, 90000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (2, 2, 50000, 10000, 40000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    -- 6일 전 결제
    (3, 3, 200000, 20000, 180000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    -- 5일 전 결제
    (4, 4, 50000, 10000, 40000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    -- 3일 전 결제
    (5, 5, 200000, 10000, 190000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (6, 6, 230000, 20000, 210000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (7, 7, 120000, 10000, 110000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    -- 2일 전 결제
    (8, 8, 400000, 20000, 380000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (9, 9, 150000, 10000, 140000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (10, 10, 150000, 10000, 140000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    -- 1일 전 결제
    (11, 11, 300000, 20000, 280000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (12, 12, 150000, 20000, 130000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (13, 13, 120000, 10000, 110000, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    -- 현재
    (14, 14, 350000, 0, 350000, 'PENDING', NOW(), NOW()),
    (15, 15, 130000, 0, 130000, 'PENDING', NOW(), NOW());