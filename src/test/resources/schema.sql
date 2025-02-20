-- users
create table users
(
    id         bigint auto_increment primary key,
    name       varchar(255) not null,
    updated_at datetime(6)  ,
    created_at datetime(6)
);

-- points
create table points
(
    id         bigint auto_increment primary key,
    user_id    bigint           not null,
    balance    bigint           not null,
    version    bigint default 0 ,
    created_at datetime(6)      ,
    updated_at datetime(6)      ,
    constraint UKswg8y3uo5dm5psbnesgeu1my unique (user_id)
);

-- point_histories
create table point_histories
(
    id         bigint auto_increment primary key,
    point_id   bigint                 not null,
    amount     bigint                 not null,
    type       enum ('CHARGE', 'USE') not null,
    created_at datetime(6)            ,
    updated_at datetime(6)
);

-- categories
create table categories
(
    id         bigint auto_increment primary key,
    name       varchar(255) not null,
    created_at datetime(6)  ,
    updated_at datetime(6)
);

-- products
create table products
(
    id             bigint auto_increment primary key,
    name           varchar(255)                                     not null,
    description    varchar(255)                                     ,
    category_id    bigint                                           not null,
    price          bigint                                           not null,
    status         enum ('ON_SALE', 'SUSPENDED', 'TEMPORARILY_OUT') not null,
    created_at     datetime(6)                                      ,
    updated_at     datetime(6)
);

-- stocks
create table stocks
(
    id         bigint auto_increment primary key,
    product_id bigint        not null ,
    quantity   int default 0 not null,
    created_at datetime(6)   ,
    updated_at datetime(6)   ,
    constraint UKhtp625bmmsb6gay567r5sdfoc unique (product_id)
);

-- orders
create table orders
(
    id               bigint auto_increment primary key,
    user_id          bigint                                                                                       not null,
    total_amount     bigint                                                                                       not null,
    status           enum ('COMPLETED', 'PAYMENT_COMPLETED', 'PAYMENT_PENDING', 'READY_FOR_SHIPPING', 'SHIPPING') not null,
    receiver_name    varchar(255)                                                                                 not null,
    receiver_phone   varchar(255)                                                                                 not null,
    shipping_address varchar(255)                                                                                 not null,
    created_at       datetime(6)                                                                                  ,
    updated_at       datetime(6)
);

-- order_items
create table order_items
(
    id           bigint auto_increment primary key,
    order_id     bigint       not null,
    product_id   bigint       not null,
    product_name varchar(255) not null,
    price        bigint       not null,
    quantity     int          not null,
    created_at   datetime(6)  ,
    updated_at   datetime(6)
);

-- payments
create table payments
(
    id              bigint auto_increment primary key,
    order_id        bigint                                    not null,
    total_amount    bigint                                    not null,
    discount_amount bigint default 0                          not null,
    final_amount    bigint                                    not null,
    status          enum ('CANCELED', 'COMPLETED', 'PENDING') not null,
    paid_at         datetime(6)                               ,
    created_at      datetime(6)                               ,
    updated_at      datetime(6)
);

-- coupons
create table coupons
(
    id                  bigint auto_increment primary key,
    name                varchar(255)                        not null,
    discount_type       enum ('FIXED_AMOUNT', 'PERCENTAGE') not null,
    discount_amount     bigint                              not null,
    min_order_amount    bigint                              null,
    max_discount_amount bigint                              null,
    valid_from          datetime(6)                         not null,
    valid_until         datetime(6)                         not null,
    total_quantity      int                                 not null,
    created_at          datetime(6)                         ,
    updated_at          datetime(6)
);

-- issued_coupons
create table issued_coupons
(
    id         bigint auto_increment primary key,
    coupon_id  bigint                     not null,
    user_id    bigint                     not null,
    status     enum ('AVAILABLE', 'USED') not null,
    used_at    datetime(6)                null,
    version    bigint default 0           ,
    created_at datetime(6)                null,
    updated_at datetime(6)                null,
    constraint uk_issued_coupon_coupon_user unique (coupon_id, user_id)
);

CREATE TABLE payment_event_outbox
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id     BIGINT                NOT NULL,
    payload        JSON                  NOT NULL,
    created_at     datetime              NOT NULL,
    published      BIT(1)                NOT NULL
);