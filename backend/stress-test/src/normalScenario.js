import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from './helpers.js'; // Ensure you have a valid UUID helper

const fullStages = [
    { duration: '1m', target: 3000 }, // Ramp up to 3000 users in 1 minute
    { duration: '1m', target: 3000 }, // Maintain 3000 users for 1 minute
    { duration: '1m', target: 0 },   // Ramp down to 0 users in 1 minute
];

const lightStages = [
    { duration: '10s', target: 10 }, // Ramp up to 10 users in 10 seconds
    { duration: '10s', target: 10 }, // Maintain 10 users for 10 seconds
    { duration: '10s', target: 0 },  // Ramp down to 0 users in 10 seconds
];

export const options = {
    stages: __ENV.RUN_MODE === 'full' ? fullStages : lightStages,
};


export default function () {
}
