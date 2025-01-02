# 포인트 관리

## 포인트 충전

### Endpoint
`PATCH /point/charge`

### Request

#### Headers
|Header|description|
|------|-----------|
|Content-Type| application/json|

#### RequestBody
```json
{
  "userId": 1,
  "amount": 10000  
}
```
|필드명|타입| 필수 | 설명     |
|---|---|-|--------|
|userId|long|Y| 사용자의 ID |
|amount|long|Y| 충전할 금액 |

### Response

#### Success Response
Status: 200 OK
```json
{
  "userId": 1,
  "name": "홍길동",
  "balance": 15000
}
```
|필드명| 타입     | 설명      |
|---|--------|---------|
|userId| long   | 사용자의 ID |
|name| String | 사용자의 이름 |
|balance| long   | 충전 후 잔액 |

### Error

| 상황                | 에러 메시지 | Mock API           |
|-------------------|--------|--------------------|
| 1회 충전 최소 금액 미달    | 최소 충전 금액(1,000원)보다 작은 금액으로 충전할 수 없습니다.   | amount < 1,000     |
| 1회 충전 최대 금액 초과 | 최대 충전 금액(1,000,000원)보다 큰 금액으로 충전할 수 없습니다. | amount > 1,000,000 |
| 포인트 잔액 한도 초과      | 포인트 한도(10,000,000원)를 초과하여 충전할 수 없습니다.   | userId == 9        |

## 포인트 조회

### Endpoint
`GET /point`

### Request

```http request
GET /point?userId=1
```
|필드명|타입| 필수 | 설명     |
|---|---|-|--------|
|userId|long|Y| 사용자의 ID |

### Response

#### Success Response
Status: 200 OK
```json
{
  "userId": 1,
  "name": "홍길동",
  "balance": 15000
}
```
|필드명| 타입     | 설명        |
|---|--------|-----------|
|userId| long   | 사용자의 ID   |
|name| String | 사용자의 이름   |
|balance| long   | 현재 포인트 잔액 |

### Error

| 상황            | 에러 메시지 | Mock API    |
|---------------|--------|-------------|
| 등록되지 않은 사용자   | 사용자를 찾을 수 없습니다.   | userId == 9 |

# 상품 정보 조회

## 카테고리별 상품 목록 조회

### Endpoint
`GET /product/list`

### Request

```http request
GET /product/list?categoryId=1&page=0&size=10&sort=PRICE&direction=DESC
```

| 필드명        | 타입                            | 필수 | 설명                                  |
|------------|-------------------------------|----|-------------------------------------|
| categoryId | long                          | Y  | 조회할 상품의 카테고리 ID                     |
| page       | int                           | Y  | 조회할 페이지 (0부터 시작)                    |
| size       | int                           | Y  | 페이지당 조회할 상품 수                       |
| sort       | SortColumn [CREATED_AT, PRICE] | N  | 정렬 기준 컬럼 <br/>(default: CREATED_AT) |
| direction  | SortDirection [ASC, DESC]     | N  | 정렬 방향 <br/> (default: ASC)          |

### Response

#### Success Response
Status: 200 OK
```json
{
  "content": [
    {
      "productId": 1,
      "name": "로봇청소기 Pro",
      "description": "반려동물 털 청소에 최적화된 신상 로봇청소기입니다",
      "category": "가전",
      "price": 399000,
      "originalPrice": 599000,
      "stock": 100,
      "status": "ON_SALE"
    },
    ...
  ],
  "pageInfo": {
    "page": 0,
    "size": 10,
    "totalElements": 42,
    "totalPages": 5
  }
}
```

| 필드명                    | 타입 | 설명|
|------------------------| --- | ---|
| content                | ProductSummary[] | 상품 요약 정보 목록|
| content.productId      | long | 상품 ID|
| content.name           | String | 상품명|
| content.description    | String | 상품 설명|
| content.category       | String | 카테고리명|
| content.price          | long | 판매가|
| content.originalPrice  | long | 정가|
| content.stock          | int | 재고수량|
| content.status         | SaleStatus [ON_SALE, TEMPORARILY_OUT, SUSPENDED] | 판매상태|
| pageInfo               | PageInfo | 페이지 정보|
| pageInfo.page          | int | 현재 페이지 번호|
| pageInfo.size          | int | 페이지 크기|
| pageInfo.totalElements | long | 전체 상품 수|
| pageInfo.totalPages    | int | 전체 페이지 수|

# 쿠폰 발급

## 쿠폰 발급 요청

### Endpoint
`POST /coupon/issue`

### Request

#### Headers

| Header       | description|
|--------------| -----------|
| Content-Type | application/json|

#### RequestBody
```json
{
  "couponId": 1,
  "userId": 1
}
```

| 필드명      | 타입 | 필수 | 설명|
|----------| --- | --- | ---|
| couponId | long | Y | 발급받을 쿠폰 ID|
| userId   | long | Y | 발급받을 사용자 ID|

### Response

#### Success Response
Status: 200 OK
```json
{
  "success": true
}
```

| 필드명     | 타입 | 설명|
|---------| --- | ---|
| success | boolean | 쿠폰 발급 성공 여부|

### Error Response

| 상황      | 에러 메시지 | Mock API|
|---------| --- | ---|
| 쿠폰 소진   | 쿠폰이 모두 소진되었습니다. | userId == 9|
| 이미 발급됨  | 이미 쿠폰이 발급되었습니다. | userId == 99|
| 발급 대기 중 | 이미 쿠폰 발급 대기 중입니다. | userId == 999|


# 주문 및 결제

## 주문 

### Endpoint
`POST /order`

### Request

#### Headers
| Header         | Description         |
|----------------|---------------------|
| Content-Type   | application/json    |

#### RequestBody

```json
{
  "userId": 1,
  "orderItems": [
    {
      "productId": 100,
      "quantity": 2
    },
    {
      "productId": 101,
      "quantity": 1
    }
  ],
  "receiverName": "홍길동",
  "receiverPhone": "010-1234-5678",
  "shippingAddress": "서울특별시 강남구 강남대로 123"
}
```

| 필드명            | 타입            | 필수 | 설명                           |
|-------------------|-----------------|------|--------------------------------|
| userId            | long            | Y    | 주문 요청한 사용자 ID           |
| orderItems        | List<OrderItem> | Y    | 주문 상품 목록                  |
| receiverName      | String          | Y    | 수령인 이름                     |
| receiverPhone     | String          | Y    | 수령인 연락처                   |
| shippingAddress   | String          | Y    | 배송 주소                       |

OrderItem

| 필드명     | 타입  | 필수 | 설명        |
|------------|-------|------|-------------|
| productId  | long  | Y    | 주문 상품 ID |
| quantity   | int   | Y    | 주문 수량    |

### Response

#### Success Response
Status: 200 OK

```json
{
  "orderId": 1,
  "paymentId": 1,
  "orderStatus": "PAYMENT_PENDING",
  "orderTime": "2025-01-01T12:00:00"
}
```

| 필드명         | 타입               | 설명                            |
|----------------|--------------------|---------------------------------|
| orderId        | long               | 생성된 주문 ID                  |
| paymentId      | long               | 결제 ID                         |
| orderStatus    | OrderStatus        | 주문 상태                       |
| orderTime      | LocalDateTime      | 주문 생성 시간                  |

### Error Response

| 상황                     | 에러 메시지                      | Mock API        |
|--------------------------|---------------------------------|-----------------|
| 주문한 상품의 재고 부족   | 주문한 상품의 재고가 부족합니다. | userId == 9     |

## 결제

### Endpoint
`PATCH /pay/order`

### Request

#### Headers
| Header         | Description         |
|----------------|---------------------|
| Content-Type   | application/json    |

#### RequestBody
```json
{
  "userId": 1,
  "orderId": 100,
  "couponId": 1
}
```
| 필드명      | 타입            | 필수 | 설명                           |
|-------------|-----------------|------|--------------------------------|
| userId      | long            | Y    | 결제를 요청한 사용자 ID         |
| orderId     | long            | Y    | 결제할 주문 ID                 |
| couponId    | Optional<long>  | N    | 결제 시 사용할 쿠폰 ID (옵션)  |

### Response

#### Success Response
Status: 200 OK

```json
{
    "orderId": 100,
    "paymentId": 1,
    "amount": 30000,
    "orderStatus": "PAYMENT_COMPLETED",
    "paymentStatus": "COMPLETED",
    "paymentTime": "2025-01-01T12:00:00"
}
```
| 필드명         | 타입               | 설명                            |
|----------------|--------------------|---------------------------------|
| orderId        | long               | 결제된 주문 ID                  |
| paymentId      | long               | 생성된 결제 ID                  |
| amount         | long               | 결제 금액                       |
| orderStatus    | OrderStatus        | 주문 상태                       |
| paymentStatus  | PaymentStatus      | 결제 상태                       |
| paymentTime    | LocalDateTime      | 결제 완료 시간                  |

### Error Response

| 상황                     | 에러 메시지             | Mock API        |
|--------------------------|-----------------------|-----------------|
| 이미 결제 완료된 주문     | 이미 결제 완료된 주문입니다. | orderId == 9    |
| 이미 결제 취소된 주문     | 이미 결제 취소된 주문입니다. | orderId == 99   |
| 이미 사용된 쿠폰          | 이미 사용 처리된 쿠폰입니다. | couponId == 9   |
| 만료된 쿠폰               | 만료된 쿠폰 입니다.     | couponId == 99  |
| 적용 불가 쿠폰            | 해당 결제에 적용 불가한 쿠폰입니다. | couponId == 999 |
| 포인트 부족               | 포인트가 부족합니다.     | userId == 9     |


# 상위 상품 조회

## 카테고리별 상위 상품 목록 조회
- 최근 3일간 카테고리별 판매량이 가장 많은 상품 5개를 반환합니다
- 판매량 순으로 정렬되어 반환됩니다

### Endpoint
`GET /product/best`

### Request

```http request
GET /product/best?categoryId=1
```

| 필드명        | 타입 | 필수 | 설명|
|------------| --- | --- | ---|
| categoryId | long | Y | 조회할 카테고리 ID|

### Response

#### Success Response
Status: 200 OK
```json
[
  {
    "id": 2,
    "name": "울트라 게이밍 마우스",
    "description": "초고성능 게이밍 마우스",
    "category": "PC주변기기",
    "price": 89000,
    "originalPrice": 129000,
    "stock": 50,
    "status": "ON_SALE"
  },
  ...
]
```

| 필드명           | 타입 | 설명|
|---------------| --- | ---|
| id            | long | 상품 ID|
| name          | String | 상품명|
| description   | String | 상품 설명|
| category      | String | 카테고리명|
| price         | long | 판매가|
| originalPrice | long | 정가|
| stock         | int | 재고수량|
| status        | SaleStatus [ON_SALE, TEMPORARILY_OUT, SUSPENDED] | 판매상태|

