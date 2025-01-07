import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from './helpers.js'; // Optional, if you create a UUID helper

export const options = {
    stages: [
        { duration: '1m', target: 1000 },
        { duration: '1m', target: 3000 },
        { duration: '1m', target: 4000 },
        { duration: '30s', target: 0 }
    ]
};

function registerPatient() {
    const url = 'http://localhost:8080/api/patients/register';
    const payload = JSON.stringify({
        firstName: 'Tooth',
        lastName: 'Broken',
        email: `test-${uuidv4()}@example.com`,
        password: 'StrongPassword123!',
        dateOfBirth: '1990-01-01',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(url, payload, params);

    check(response, {
        'is status 201': (r) => r.status === 201,
        'has success message': (r) => r.body && r.body.includes('Patient registered successfully!')
    });

    return response;
}


export default function () {

    const registrationResponse = registerPatient();
    sleep(1);
}