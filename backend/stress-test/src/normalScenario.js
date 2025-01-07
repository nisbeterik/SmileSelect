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

let clinicCreated = false;
let clinicId = null;

function createClinic() {
    const url = 'http://localhost:8080/api/clinics';
    const payload = JSON.stringify({
        name: 'Clinic-stress-test',
        longitude: 11.9746, // Example longitude for Gothenburg
        latitude: 57.7089,  // Example latitude for Gothenburg
        street: 'Kungsportsavenyn', // A well-known street in Gothenburg
        zip: '41136', // A zip code in Gothenburg
        city: 'Gothenburg',
        houseNumber: '5' // Example house number
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const response = http.post(url, payload, params);

    check(response, {
        'clinic created': (r) => r.status === 201,
    });


    clinicCreated = true;
    return JSON.parse(response.body).id;
}

export default function () {
    clinicId = createClinic();
    sleep(1)
}
