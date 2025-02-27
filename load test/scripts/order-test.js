import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 100 },
        { duration: '2m', target: 300 },
        { duration: '2m', target: 500 },
        { duration: '2m', target: 700 },
        { duration: '2m', target: 1000 },
        { duration: '1m', target: 100 },
    ],
};

const API_BASE_URL = 'http://host.docker.internal:8080';
const ORDER_ENDPOINT = `${API_BASE_URL}/order`;

const RECEIVER_NANE = '이다은';
const RECEIVER_PHONE = '010-1111-2222';
const SHIPPING_ADDRESS = '서울시 광진구 능동로';

export default function() {
    const url = ORDER_ENDPOINT;

    const randomUserId = Math.floor(Math.random() * 150000) + 1;
    const randomProductId = Math.floor(Math.random() * 150000) + 1;
    const randomQuantity = Math.floor(Math.random() * 3) + 1

    const payload = JSON.stringify({
        userId: randomUserId,
        orderItems: [
            {
                productId: randomProductId,
                quantity: randomQuantity
            }
        ],
        receiverName: RECEIVER_NANE,
        receiverPhone: RECEIVER_PHONE,
        shippingAddress: SHIPPING_ADDRESS,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 201 or 400 or 404': (r) => [201, 400, 404].includes(r.status),
    });

    console.log(`User ${randomUserId}: Response status ${res.status}`);

    sleep(1);
}