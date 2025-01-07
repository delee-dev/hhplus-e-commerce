TRUNCATE TABLE coupons;

INSERT INTO coupons (id, name, discount_type, discount_amount, min_order_amount, max_discount_amount, valid_from, valid_until, total_quantity, created_at, updated_at) VALUES
    (1, '신규가입 할인', 'FIXED_AMOUNT', 10000, 50000, 10000, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_ADD(NOW(), INTERVAL 1 MONTH), 1000, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));
