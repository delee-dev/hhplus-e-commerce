import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 1000 },
        { duration: '2m', target: 2500 },
        { duration: '2m', target: 5000 },
        { duration: '1m', target: 1000 },
    ],
};

const API_BASE_URL = 'http://host.docker.internal:8080';
const BEST_SELLER_ENDPOINT = `${API_BASE_URL}/products/best?categoryId=`;

export default function() {
    const randomCategoryId = Math.floor(Math.random() * 10) + 1;
    const url = BEST_SELLER_ENDPOINT + randomCategoryId;

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.get(url, params);

    check(res, {
        'status is 200': (r) => r.status == 200,
    });

    console.log(`CategoryId ${randomCategoryId}: Response status ${res.status}`);

    sleep(1);
}