
- [결제 조회](#1-결제-조회)
  - [현황 분석](#1-현황-분석)
  - [성능 문제 분석](#2-성능-문제-분석)
  - [개선 방안](#3-개선-방안)
  - [개선 결과](#4-개선-결과)

- [상품 조회](#2-상품-조회)
  - [현황 분석](#1-현황-분석-1)
  - [성능 문제 분석](#2-성능-문제-분석-1)
  - [개선 방안](#3-개선-방안-1)
  - [개선 결과](#4-개선-결과-1)
    - [category_id 단일 인덱스](#category_id-단일-인덱스)
    - [(category_id, status) 복합 인덱스](#category_id-status-복합-인덱스)
    - [(category_id, status, created_at) 복합 인덱스](#category_id-status-created_at-복합-인덱스)
    - [(category_id, created_at) 복합 인덱스](#category_id-created_at-복합-인덱스)
  - [결론](#결론)

- [상위 상품 조회](#3-상위-상품-조회)
  - [현황 분석](#1-현황-분석-2)
  - [성능 문제 분석](#2-성능-문제-분석-2)
  - [개선 방안](#3-개선-방안-2)
  - [개선 결과](#4-개선-결과-2)
    - [order_items.order_id 컬럼 인덱스](#order_itemsorder_id-컬럼-인덱스)
    - [payments.order_id 컬럼 인덱스](#paymentsorder_id-컬럼-인덱스)
  - [결론](#결론-1)

- [분석 제외 쿼리](#4-분석-제외-쿼리)
  - [Primary Key로 조회하는 경우](#primary-key로-조회하는-경우)
  - [UNIQUE 제약 조건이 있는 컬럼으로 조회하는 경우](#unique-제약-조건이-있는-컬럼으로-조회하는-경우)
  - [전체 데이터를 조회하는 쿼리](#전체-데이터를-조회하는-쿼리)

# 결제 조회

## 1. 현황 분석

### 대상 쿼리
`PaymentRepository.findByOrderIdWithLock(Long orderId)`
```sql
select p1_0.id,p1_0.created_at,p1_0.discount_amount,p1_0.final_amount,p1_0.order_id,p1_0.paid_at,p1_0.status,p1_0.total_amount,p1_0.updated_at 
from payments p1_0 
where p1_0.order_id=? 
for update
```
### 테이블 현황
- 전체 레코드 수: 146,642건
- 기존 인덱스: PRIMARY (id)

## 2. 성능 문제 분석

`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | p1\_0 | null | ALL | null | null | null | null | 145824 | 10 | Using where |

`EXPLAIN ANALYZE`

```text
-> Filter: (p1_0.order_id = 1)  (cost=14799 rows=14582) (actual time=1.96..84.1 rows=1 loops=1)
    -> Table scan on p1_0  (cost=14799 rows=145824) (actual time=1.88..77 rows=146642 loops=1)
```
### 문제점
테이블 스캔 문제
- 스캔 레코드: 146,642건 모두 스캔

## 3. 개선 방안

### `order_id` 컬럼 인덱스
```sql
CREATE INDEX idx_payments_order_id ON payments (order_id);
```

## 4. 개선 결과
`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | p1\_0 | null | ref | idx\_payments\_order\_id | idx\_payments\_order\_id | 8 | const | 1 | 100 | null |

`EXPLAIN ANALYZE`
```text
-> Index lookup on p1_0 using idx_payments_order_id (order_id=1)  (cost=0.35 rows=1) (actual time=0.484..0.51 rows=1 loops=1)
```

### 쿼리 성능 개선
- 실행 시간: 84.1ms → 0.51ms
- 예상 비용: 14,799 → 0.35
- 스캔 방식: Table Full Scan → Index Lookup
- 스캔 레코드: 146,642건 → 1건

# 상품 조회

## 1. 현황 분석

### 대상 쿼리

`ProductRepository.findProductsByCategoryIdAndStatusNot(long categoryId, SaleStatus status, int page, int size, String sortColumn, String sortDirection)`

```sql
select p1_0.id,p1_0.category_id,p1_0.created_at,p1_0.description,p1_0.name,p1_0.price,p1_0.status,p1_0.updated_at 
from products p1_0 
    left join categories c1_0 
        on c1_0.id=p1_0.category_id 
where c1_0.id=? and p1_0.status<>? 
order by p1_0.created_at 
limit ?
```

### 테이블 현황
- products
    - 전체 레코드 수: 150,000건
    - 기존 인덱스: PRIMARY (id)
- categories
  전체 레코드 수: 10건
    - 기존 인덱스: PRIMARY (id)

## 2. 성능 문제 분석

`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | c1\_0 | null | const | PRIMARY | PRIMARY | 8 | const | 1 | 100 | Using index; Using filesort |
| 1 | SIMPLE | p1\_0 | null | ALL | null | null | null | null | 148731 | 6.67 | Using where |

`EXPLAIN ANALYZE`
```text
-> Limit: 10 row(s)  (cost=15202 rows=10) (actual time=85.4..85.4 rows=10 loops=1)
    -> Sort: p1_0.created_at, limit input to 10 row(s) per chunk  (cost=15202 rows=148731) (actual time=85.4..85.4 rows=10 loops=1)
        -> Filter: ((p1_0.category_id = 1) and (p1_0.`status` <> 'SUSPENDED'))  (cost=15202 rows=148731) (actual time=0.306..83.3 rows=12358 loops=1)
            -> Table scan on p1_0  (cost=15202 rows=148731) (actual time=0.274..73.1 rows=150000 loops=1)
```

### 문제점
테이블 스캔 문제
- 스캔 레코드: 150,000행
- `products` 테이블의 150,000건 모두 스캔

필터링 비효율 문제
- 스캔 레코드: 150,000행
- 필터링된 레코드: 12,258행 (약 8.2%만 유효한 레코드)
- 인덱스 없이 모든 행에 대해 필터 조건 검사

정렬 문제
- 필터링된 12,358행에 대해 메모리상에서 정렬 수행

## 3. 개선 방안

### 인덱스가 필요한 컬럼
JOIN 절 컬럼
- `categories.id` (기존 인덱스)
- `products.category_id`

WHERE 절 컬럼
- `categories.id` (기존 인덱스)
- `products.status`

ORDER BY 절 컬럼
- `products.created_at`

결론적으로, `products` 테이블의 `category_id`, `status`, `created_at` 컬럼에 대한 복합 인덱스가 필요합니다.

## 4. 개선 결과

### `category_id` 단일 인덱스
`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | p1\_0 | null | ref | idx\_products\_category\_id | idx\_products\_category\_id | 8 | const | 29534 | 66.67 | Using where; Using filesort |

`EXPLAIN ANALYZE`
```text
-> Limit: 10 row(s)  (cost=2955 rows=10) (actual time=36.5..36.5 rows=10 loops=1)
    -> Sort: p1_0.created_at, limit input to 10 row(s) per chunk  (cost=2955 rows=29534) (actual time=36.5..36.5 rows=10 loops=1)
        -> Filter: (p1_0.`status` <> 'SUSPENDED')  (cost=2955 rows=29534) (actual time=0.192..34.2 rows=12358 loops=1)
            -> Index lookup on p1_0 using idx_products_category_id (category_id=1)  (cost=2955 rows=29534) (actual time=0.187..32.4 rows=15000 loops=1)
```
#### 쿼리 성능 개선
- 실행 시간: 85.4ms → 36.5ms
- 예상 비용: 15,202 → 2,955
- 스캔 방식: Table Full Scan → Index Lookup

#### 비교
- 전체 테이블 스캔에서 인덱스 탐색으로 개선
- 여전히 `status` 필터링과 `created_at` 정렬 비용 발생

### (`category_id`, `status`) 복합 인덱스
`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | p1\_0 | null | ref | idx\_products\_category\_id\_status | idx\_products\_category\_id\_status | 8 | const | 29560 | 66.67 | Using index condition; Using filesort |

`EXPLAIN ANALYZE`
```text
-> Limit: 10 row(s)  (cost=2957 rows=10) (actual time=30.8..30.8 rows=10 loops=1)
    -> Sort: p1_0.created_at, limit input to 10 row(s) per chunk  (cost=2957 rows=29560) (actual time=30.8..30.8 rows=10 loops=1)
        -> Index lookup on p1_0 using idx_products_category_id_status (category_id=1), with index condition: (p1_0.`status` <> 'SUSPENDED')  (cost=2957 rows=29560) (actual time=0.339..28.9 rows=12358 loops=1)
```
#### 쿼리 성능 개선
- 실행 시간: 85.4ms → 30.8ms
- 예상 비용: 15,202 → 2,957
- 스캔 방식: Table Full Scan → Index Lookup with condition

#### 비교
- `status`로 필터링 되는 데이터가 적어 단일 인덱스와 비교했을 때 비슷한 효과
- 여전히 `created_at` 정렬 비용 발생


### (`category_id`, `status`, `created_at`) 복합 인덱스
`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | p1\_0 | null | ref | idx\_products\_category\_id\_status\_created\_at | idx\_products\_category\_id\_status\_created\_at | 8 | const | 29198 | 66.67 | Using index condition; Using filesort |

`EXPLAIN ANALYZE`

```text
-> Limit: 10 row(s)  (cost=2933 rows=10) (actual time=34.6..34.6 rows=10 loops=1)
    -> Sort: p1_0.created_at, limit input to 10 row(s) per chunk  (cost=2933 rows=29198) (actual time=34.6..34.6 rows=10 loops=1)
        -> Index lookup on p1_0 using idx_products_category_id_status_created_at (category_id=1), with index condition: (p1_0.`status` <> 'SUSPENDED')  (cost=2933 rows=29198) (actual time=0.372..32.8 rows=12358 loops=1)
```
#### 쿼리 성능 개선
- 실행 시간: 85.4ms → 34.6ms
- 예상 비용: 15,202 → 2,933
- 스캔 방식: Table Full Scan → Index Lookup with condition

#### 비교
- `status`의 범위 조건으로 인해 `created_at` 인덱스 활용 불가
- (`category_id`, `status`) 복합 인덱스와 비슷한 성능 개선 효과
- 오히려 불필요한 인덱스 크기 증가를 야기할 수 있음

### (`category_id`, `created_at`) 복합 인덱스 
`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | p1\_0 | null | ref | idx\_products\_category\_id\_created\_at | idx\_products\_category\_id\_created\_at | 8 | const | 29284 | 66.67 | Using where |

`EXPLAIN ANALYZE`

```text
-> Limit: 10 row(s)  (cost=2939 rows=10) (actual time=0.248..0.255 rows=10 loops=1)
    -> Filter: (p1_0.`status` <> 'SUSPENDED')  (cost=2939 rows=19523) (actual time=0.247..0.253 rows=10 loops=1)
        -> Index lookup on p1_0 using idx_products_category_id_created_at (category_id=1)  (cost=2939 rows=29284) (actual time=0.243..0.247 rows=10 loops=1)
```

#### 쿼리 성능 개선
- 실행 시간: 85.4ms → 0.255ms
- 예상 비용: 15,202 → 2,939
- 스캔 방식: Table Full Scan → Index Lookup

#### 비교
- 가장 큰 성능 개선 효과
- `category_id` 필터링 후 `created_at`으로 정렬 최적화
- 범위 조건인 `status`를 인덱스에서 제외하여 효율성 증가

### 결론
(`category_id`, `created_at`) 복합 인덱스 사용으로 최적의 성능 달성이 가능합니다.

하지만 현재 프로젝트 상황에서는 정렬 조건이 동적으로 변경될 수 있습니다.

그렇다고 해서, 모든 정렬 조건에 대해 인덱스를 만드는 것은 비효율적이기 때문에,
정렬을 제외하고 가장 효율적인 필터링 조건으로만 인덱스를 구성하는 것이 바람직합니다.

따라서, (`category_id`, `status`) 복합 인덱스를 사용하는 것으로 결정했습니다.

# 상위 상품 조회

## 1. 현황 분석

### 대상 쿼리

`ProductRepository.findBestSellingProductsByCategory(long categoryId, int period, int limit)`

```sql
select p1_0.id,p1_0.category_id,p1_0.created_at,p1_0.description,p1_0.name,p1_0.price,p1_0.status,p1_0.updated_at 
from order_items oi1_0 
    join products p1_0 
        on oi1_0.product_id=p1_0.id 
    join categories c1_0 
        on c1_0.id=p1_0.category_id 
    join payments p2_0 
        on oi1_0.order_id=p2_0.order_id 
where c1_0.id=? 
  and p2_0.paid_at between ? and ? 
  and p2_0.status=? 
group by p1_0.id 
order by sum(oi1_0.quantity) desc 
limit ?
```
### 테이블 현황
- products
  - 전체 레코드 수: 150,000건
  - 기존 인덱스: PRIMARY (id)
- categories
  전체 레코드 수: 10건
    - 기존 인덱스: PRIMARY (id)
- payments
  - 전체 레코드 수: 146,642건
  - 기존 인덱스: PRIMARY (id)
- order_items
  - 전체 레코드 수: 440,178건
  - 기존 인덱스: PRIMARY (id)

## 2. 성능 문제 분석

`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | c1\_0 | null | const | PRIMARY | PRIMARY | 8 | const | 1 | 100 | Using index; Using temporary; Using filesort |
| 1 | SIMPLE | p2\_0 | null | ALL | null | null | null | null | 146065 | 3.7 | Using where |
| 1 | SIMPLE | oi1\_0 | null | ALL | null | null | null | null | 426448 | 10 | Using where; Using join buffer \(hash join\) |
| 1 | SIMPLE | p1\_0 | null | eq\_ref | PRIMARY | PRIMARY | 8 | hhplus.oi1\_0.product\_id | 1 | 10 | Using where |


`EXPLAIN ANALYZE`

```text
-> Limit: 5 row(s)  (actual time=745..745 rows=5 loops=1)
    -> Sort: `sum(oi1_0.quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=745..745 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=743..744 rows=8524 loops=1)
            -> Aggregate using temporary table  (actual time=743..743 rows=8524 loops=1)
                -> Nested loop inner join  (cost=254e+6 rows=23.1e+6) (actual time=68..716 rows=12836 loops=1)
                    -> Inner hash join (oi1_0.order_id = p2_0.order_id)  (cost=231e+6 rows=231e+6) (actual time=67.9..459 rows=131904 loops=1)
                        -> Table scan on oi1_0  (cost=0.945 rows=426448) (actual time=0.0387..84.5 rows=440178 loops=1)
                        -> Hash
                            -> Filter: ((p2_0.`status` = 'COMPLETED') and (p2_0.paid_at between <cache>((now() - interval 3 day)) and <cache>(now())))  (cost=10495 rows=5409) (actual time=0.7..61.1 rows=43937 loops=1)
                                -> Table scan on p2_0  (cost=10495 rows=146065) (actual time=0.683..38.1 rows=146642 loops=1)
                    -> Filter: (p1_0.category_id = 1)  (cost=46.2e-6 rows=0.1) (actual time=0.00184..0.00185 rows=0.0973 loops=131904)
                        -> Single-row index lookup on p1_0 using PRIMARY (id=oi1_0.product_id)  (cost=46.2e-6 rows=1) (actual time=0.00164..0.00167 rows=0.977 loops=131904)
```

### 문제점

`EXPLAIN ANALYZE` 결과를 보았을 때, 성능 병목의 가장 큰 원인은 `order_items`와 `payment` 테이블 간의 `Inner hash join` 연산에서 발생합니다.

```text
-> Inner hash join (oi1_0.order_id = p2_0.order_id)  (cost=231e+6 rows=231e+6) (actual time=67.9..459 rows=131904 loops=1)
```

이 조인 과정에서 231백만의 비용이 발생하며, 131,904개의 대량 중간 결과를 생성하여 `loop`를 증가 시키는 원인이 됩니다.

주목할 점은 각 테이블의 스캔 비용(`order_items`: 0.945, `payment`: 10,495)은 상대적으로 매우 낮으며, 실제 비용은 두 테이블의 데이터를 결합하는 과정에서 발생한다는 점입니다.

## 3. 개선 방안

개선 방안은 `order_items`와 `payment` 두 테이블의 조인 비용을 줄이는 것입니다. 

이를 위해 조인 컬럼(`order_id`)에 인덱스를 추가하는 방법을 고려할 수 있습니다.

## 4. 개선 결과

### `order_items.order_id` 컬럼 인덱스

```sql
create index idx_order_items_order_id on order_items(order_id);
```

`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | c1\_0 | null | const | PRIMARY | PRIMARY | 8 | const | 1 | 100 | Using index; Using temporary; Using filesort |
| 1 | SIMPLE | p2\_0 | null | ALL | null | null | null | null | 145864 | 3.7 | Using where |
| 1 | SIMPLE | oi1\_0 | null | ref | idx\_order\_items\_order\_id | idx\_order\_items\_order\_id | 8 | hhplus.p2\_0.order\_id | 2 | 100 | null |
| 1 | SIMPLE | p1\_0 | null | eq\_ref | PRIMARY | PRIMARY | 8 | hhplus.oi1\_0.product\_id | 1 | 10 | Using where |

`EXPLAIN ANALYZE`

```text
-> Limit: 5 row(s)  (actual time=763..763 rows=5 loops=1)
    -> Sort: `sum(oi1_0.quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=763..763 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=760..762 rows=8497 loops=1)
            -> Aggregate using temporary table  (actual time=760..760 rows=8497 loops=1)
                -> Nested loop inner join  (cost=21976 rows=1603) (actual time=2..729 rows=12813 loops=1)
                    -> Nested loop inner join  (cost=16089 rows=16025) (actual time=1.94..434 rows=131762 loops=1)
                        -> Filter: ((p2_0.`status` = 'COMPLETED') and (p2_0.paid_at between <cache>((now() - interval 3 day)) and <cache>(now())))  (cost=10481 rows=5402) (actual time=0.706..52.2 rows=43831 loops=1)
                            -> Table scan on p2_0  (cost=10481 rows=145864) (actual time=0.64..31.9 rows=146642 loops=1)
                        -> Index lookup on oi1_0 using idx_order_items_order_id (order_id=p2_0.order_id)  (cost=0.742 rows=2.97) (actual time=0.00753..0.00845 rows=3.01 loops=43831)
                    -> Filter: (p1_0.category_id = 1)  (cost=0.267 rows=0.1) (actual time=0.00213..0.00213 rows=0.0972 loops=131762)
                        -> Single-row index lookup on p1_0 using PRIMARY (id=oi1_0.product_id)  (cost=0.267 rows=1) (actual time=0.00194..0.00197 rows=0.977 loops=131762)
```

#### 쿼리 성능 개선
- 예상 비용: 254,000,000 → 21,976
- 스캔 방식: Hash Join + Full Table Scan → Nested Loop Join + Index Lookup

#### 비교
- 조인 전략이 Hash Join에서 Nested Loop Join으로 변경되어 메모리 사용량 감소
- 전체 실행 시간은 소폭 증가했으나, 시스템 리소스 사용 측면에서 큰 성능 향상

### `payments.order_id` 컬럼 인덱스

```sql
create index idx_payments_order_id on payments(order_id);
```
`EXPLAIN`

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | c1\_0 | null | const | PRIMARY | PRIMARY | 8 | const | 1 | 100 | Using index; Using temporary; Using filesort |
| 1 | SIMPLE | oi1\_0 | null | ALL | null | null | null | null | 437888 | 100 | null |
| 1 | SIMPLE | p2\_0 | null | ref | idx\_payments\_order\_id | idx\_payments\_order\_id | 8 | hhplus.oi1\_0.order\_id | 1 | 5 | Using where |
| 1 | SIMPLE | p1\_0 | null | eq\_ref | PRIMARY | PRIMARY | 8 | hhplus.oi1\_0.product\_id | 1 | 10 | Using where |

`EXPLAIN ANALYZE`

```text
-> Limit: 5 row(s)  (actual time=2038..2038 rows=5 loops=1)
    -> Sort: `sum(oi1_0.quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=2038..2038 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=2034..2036 rows=8488 loops=1)
            -> Aggregate using temporary table  (actual time=2034..2034 rows=8488 loops=1)
                -> Nested loop inner join  (cost=205726 rows=2189) (actual time=5.96..1990 rows=12791 loops=1)
                    -> Nested loop inner join  (cost=197683 rows=21894) (actual time=0.988..1631 rows=131597 loops=1)
                        -> Table scan on oi1_0  (cost=44422 rows=437888) (actual time=0.336..176 rows=440178 loops=1)
                        -> Filter: ((p2_0.`status` = 'COMPLETED') and (p2_0.paid_at between <cache>((now() - interval 3 day)) and <cache>(now())))  (cost=0.25 rows=0.05) (actual time=0.0031..0.00319 rows=0.299 loops=440178)
                            -> Index lookup on p2_0 using idx_payments_order_id (order_id=oi1_0.order_id)  (cost=0.25 rows=1) (actual time=0.00272..0.00294 rows=1 loops=440178)
                    -> Filter: (p1_0.category_id = 1)  (cost=0.267 rows=0.1) (actual time=0.00262..0.00263 rows=0.0972 loops=131597)
                        -> Single-row index lookup on p1_0 using PRIMARY (id=oi1_0.product_id)  (cost=0.267 rows=1) (actual time=0.00243..0.00246 rows=0.977 loops=131597)
```
#### 쿼리 성능 개선
- 실행 시간: 745ms → 2,038ms (약 173% 증가)
- 예상 비용: 254,000,000 → 205,726
- 스캔 방식: Hash Join → Nested Loop Join + Index Lookup on payments

#### 비교
- 조인 전략이 Hash Join에서 Nested Loop Join으로 변경되어 메모리 사용량 감소, 시스템 리소스 사용 측면에서 성능 향상
- 대량의 데이터(440,178행)에 대해 건별 인덱스 조회 발생, Nested Loop Join이 오히려 대용량 데이터 처리에는 비효율적이어서 성능 감소

### 결론
`order_items.order_id` 컬럼 인덱스 사용으로 성능 병목을 개선할 수 있습니다.

현재 상위 상품 조회 기능의 경우, 캐시를 사용하여 DB 부하를 줄이고 있습니다.

따라서 현재 `order_items.order_id` 인덱스 추가만으로도 충분한 성능 개선이 가능할 것으로 보입니다.

# 분석 제외 쿼리

## Primary Key로 조회하는 경우
MySQL InnoDB 스토리지 엔진에서 PK는 자동으로 인덱스가 생성됩니다.

따라서, **PK만을 조회 조건으로 하는 쿼리**의 경우 자동 생성된 인덱스를 사용하여 실행되기 때문에 인덱스 추가 대상에서 **제외**했습니다.

### 예시

유저 테이블의 인덱스를 조회해보면 다음과 같이 `PRIMARY` 라는 이름의 인덱스가 존재함을 확인할 수 있고, 이는 PK인 `id` 컬럼에 대한 인덱스임을 확인할 수 있습니다.

| Table | Non\_unique | Key\_name | Seq\_in\_index | Column\_name | Collation | Cardinality | Sub\_part | Packed | Null | Index\_type | Comment | Index\_comment | Visible | Expression |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| users | 0 | PRIMARY | 1 | id | A | 149778 | null | null |  | BTREE |  |  | YES | null |

PK만을 사용해서 조회하는 `UserRepository.findById(Long id)` 를 실행해보면, 아래와 같은 쿼리가 실행됩니다.

 ```sql
 select u1_0.id,u1_0.created_at,u1_0.name,u1_0.updated_at 
 from users u1_0 
 where u1_0.id=?
 ```

쿼리를 `EXPLAIN` 명령어를 통해 실행 계획을 확인해보면 `PRIMARY` 인덱스를 사용해서 조회할 예정임을 알 수 있습니다.

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | u1\_0 | null | const | PRIMARY | PRIMARY | 8 | const | 1 | 100 | null |

쿼리를 `EXPLAIN ANALYZE` 명령어를 통해 분석해보면 실제로 비용을 거의 들이지 않고, 정확히 1개의 행을 읽어 왔음을 확인할 수 있습니다.

```text
-> Rows fetched before execution  (cost=0..0 rows=1) (actual time=84e-6..125e-6 rows=1 loops=1)
```


## UNIQUE 제약 조건이 있는 컬럼으로 조회하는 경우
PK와 마찬가지로 `UNIQUE` 제약 조건 또한 자동으로 인덱스가 생성됩니다.

따라서, **`UNIQUE` 제약 조건이 걸린 컬럼만을 조회 조건으로 하는 쿼리**의 경우 자동 생성된 인덱스를 사용하여 실행되기 때문에 인덱스 추가 대상에서 **제외**했습니다.

### 예시

포인트 테이블의 인덱스를 조회해보면 다음과 같이 `PRIMARY` 인덱스와 `user_id` 인덱스를 확인할 수 있습니다.

이 중, `user_id` 인덱스는 `UNIQUE` 제약 조건이 걸린 `user_id` 컬럼을 대상으로 자동 생성된 인덱스입니다.


| Table | Non\_unique | Key\_name | Seq\_in\_index | Column\_name | Collation | Cardinality | Sub\_part | Packed | Null | Index\_type | Comment | Index\_comment | Visible | Expression |
| :--- | :--- |:----------| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| points | 0 | PRIMARY   | 1 | id | A | 149632 | null | null |  | BTREE |  |  | YES | null |
| points | 0 | user_id   | 1 | user\_id | A | 149632 | null | null |  | BTREE |  |  | YES | null |

`user_id` 컬럼만을 조회 조건으로 사용하는 `PointRepository.findByUserId(Long userId)` 를 실행해보면, 아래와 같은 쿼리가 실행됩니다.

```sql
select p1_0.id,p1_0.balance,p1_0.created_at,p1_0.updated_at,p1_0.user_id,p1_0.version 
from points p1_0 
where p1_0.user_id=?
```

쿼리를 `EXPLAIN` 명령어를 통해 실행 계획을 확인해보면 생성되어 있는 `user_id` 인덱스를 사용해서 조회할 예정임을 알 수 있습니다.

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | p1\_0 | null | const | user\_id | user\_id | 8 | const | 1 | 100 | null |

쿼리를 `EXPLAIN ANALYZE` 명령어를 통해 분석해보면 실제로 비용을 거의 들이지 않고, 정확히 1개의 행을 읽어 왔음을 확인할 수 있습니다.

```text
-> Rows fetched before execution  (cost=0..0 rows=1) (actual time=208e-6..291e-6 rows=1 loops=1)
```

## 전체 데이터를 조회하는 쿼리
전체 데이터를 조회하는 쿼리의 경우, 풀 스캔이 의도된 동작이기 때문에 인덱스 추가 대상에서 **제외**했습니다.

### 예시

`CategoryRepository.findAll()`을 실행해보면, 아래와 같은 쿼리가 실행됩니다.

```sql
select c1_0.id,c1_0.created_at,c1_0.name,c1_0.updated_at 
from categories c1_0
```
쿼리를 `EXPLAIN` 명령어를 통해 실행 계획을 확인해보면 `type: ALL`로 테이블을 풀 스캔 예정임을 알 수 있습니다.

| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | c1\_0 | null | ALL | null | null | null | null | 10 | 100 | null |

쿼리를 `EXPLAIN ANALYZE` 명령어를 통해 분석해보면 실제로 테이블을 풀 스캔했음을 확인할 수 있습니다.

```text
-> Table scan on c1_0  (cost=1.25 rows=10) (actual time=0.288..0.352 rows=10 loops=1)
```

