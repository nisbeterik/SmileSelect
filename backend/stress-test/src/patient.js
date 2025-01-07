import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from './helpers.js';

const fullStages = [
    { duration: '1m', target: 3000 }, // Ramp up to 3000 users in 1 minute
    { duration: '1m', target: 3000 }, // Maintain 3000 users for 1 minutes
    { duration: '1m', target: 0 },   // Ramp down to 0 users in 1 minute
];

const lightStages = [
    { duration: '10s', target: 10 }, // Ramp up to 10 users in 10 seconds
    { duration: '10s', target: 10 }, // Maintain 10 users for 10 seconds
    { duration: '10s', target: 0 },  // Ramp down to 0 users in 10 seconds
];

// Dynamically set stages based on the environment variable
export const options = {
    stages: __ENV.RUN_MODE === 'full' ? fullStages : lightStages,
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