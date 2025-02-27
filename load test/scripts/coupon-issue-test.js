import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 2000 },
        { duration: '20s', target: 3000 },
        { duration: '20s', target: 5000 },
        { duration: '10s', target: 2000 },
    ],
};

const COUPON_ID = 3;
const API_BASE_URL = 'http://host.docker.internal:8080';
const COUPON_ISSUE_ENDPOINT = `${API_BASE_URL}/coupon/issue`;
const INIT_COUPON_ENDPOINT = `${API_BASE_URL}/coupon/quantity/initialize/${COUPON_ID}`

export function setup() {
    http.post(INIT_COUPON_ENDPOINT);
}

export default function() {
    const url = COUPON_ISSUE_ENDPOINT;

    const randomUserId = Math.floor(Math.random() * 150000) + 1;

    const payload = JSON.stringify({
        userId: randomUserId,
        couponId: COUPON_ID
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 201': (r) => r.status == 201,
    });

    console.log(`User ${randomUserId}: Response status ${res.status}`);

    sleep(1);
}