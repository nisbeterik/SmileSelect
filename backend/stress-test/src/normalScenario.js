import http from 'k6/http';
import {check, sleep} from 'k6';
import {uuidv4} from './helpers.js'; // Ensure you have a valid UUID helper

const fullStages = [
    {duration: '1m', target: 3000}, // Ramp up to 3000 users in 1 minute
    {duration: '1m', target: 3000}, // Maintain 3000 users for 1 minute
    {duration: '1m', target: 0},   // Ramp down to 0 users in 1 minute
];

const lightStages = [
    {duration: '10s', target: 10}, // Ramp up to 10 users in 10 seconds
    {duration: '10s', target: 10}, // Maintain 10 users for 10 seconds
    {duration: '10s', target: 0},  // Ramp down to 0 users in 10 seconds
];

export const options = {
    stages: __ENV.RUN_MODE === 'full' ? fullStages : lightStages,
};

let clinicCreated = false;
let clinicId = null;
let dentistId = null;
let dentistCreated = false;
let dentistCredentials = null;
let dentistLoggedIn = false;
let dentistToken = null;

function createClinic() {
    const url = 'http://localhost:8080/api/dentists/clinics';
    const payload = JSON.stringify({
        name: `Clinic-${uuidv4()}`,
        longitude: 11.9746,
        latitude: 57.7089,
        street: 'Kungsportsavenyn',
        zip: 41136,
        city: 'Gothenburg',
        houseNumber: '5'
    });

    const params = {
        headers: {'Content-Type': 'application/json'},
    };

    const response = http.post(url, payload, params);

    check(response, {
        'clinic created': (r) => r.status === 201,
    });

    // Handle possible error response
    if (response.status === 201) {
        clinicCreated = true;
        return JSON.parse(response.body).id;
    } else {
        console.error(`Failed to create clinic. Status: ${response.status}, Response: ${response.body}`);
        return null;  // Or handle appropriately
    }
}

function registerDentist(clinicId) {
    const url = `http://localhost:8080/api/dentists/register`;
    const payload = JSON.stringify({
        firstName: 'Dent',
        lastName: 'Ist',
        email: `dentist-${uuidv4()}@example.com`,
        password: 'StrongPassword123!',
        clinicId: `${clinicId}`
    });

    const params = {
        headers: {'Content-Type': 'application/json'},
    };

    const response = http.post(url, payload, params);

    check(response, {
        "Dentist registered successfully!": (r) => r.status === 201,
    });

    const parsedPayload = JSON.parse(payload);
    dentistCredentials = { email: parsedPayload.email, password: parsedPayload.password };

    dentistCreated = true;
}

function loginDentist(credentials) {
    const url = `http://localhost:8080/api/auth/login`;
    const payload = JSON.stringify({
        email: credentials.email,
        password: credentials.password,
        role: 'DENTIST'
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const response = http.post(url, payload, params);

    check(response, {
        'dentist logged in': (r) => r.status === 200,
    });

    dentistId = JSON.parse(response.body).id;

    return JSON.parse(response.body).token;
}

function createAppointment(dentistToken) {
    const url = `http://localhost:8080/api/appointments`;
    const payload = JSON.stringify({
        patientId: null,
        dentistId: `${dentistId}`,
        clinicId: `${clinicId}`,
        startTime: '2025-04-10T10:00:00Z',
        endTime: '2025-04-10T11:00:00Z'
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${dentistToken}`,
        },
    };

    const response = http.post(url, payload, params);

    check(response, {
        'appointment created': (r) => r.status === 201,
    });

    if (response.status !== 201) {
        console.error(`Failed to create appointment. Status: ${response.status}`);
        console.error(`Response body: ${response.body}`);
        return null;
    }

    const responseBody = JSON.parse(response.body);
    return responseBody.id;
}

function registerPatient() {
    const url = 'http://localhost:8080/api/patients/register';
    const patientData = {
        firstName: 'Tooth',
        lastName: 'Broken',
        email: `test-${uuidv4()}@example.com`,
        password: 'StrongPassword123!',
        dateOfBirth: '1990-01-01',
    };

    const payload = JSON.stringify(patientData);

    const params = {
        headers: {'Content-Type': 'application/json'},
    };

    const response = http.post(url, payload, params);

    check(response, {
        'patient registered': (r) => r.status === 201,
    });


    return {
        email: patientData.email,
        password: patientData.password
    };
}


function loginPatient(credentials) {
    const url = 'http://localhost:8080/api/auth/login';
    const payload = JSON.stringify({
        email: credentials.email,
        password: credentials.password,
        role: 'PATIENT'
    });

    const params = {
        headers: {'Content-Type': 'application/json'},
    };

    const response = http.post(url, payload, params);

    check(response, {
        'patient logged in': (r) => r.status === 200,
    });

    const responseBody = JSON.parse(response.body);
    return {
        token: responseBody.token,
        id: responseBody.id,
    };
}



function bookAppointment(patientId, patientToken, appointmentId) {
    const url = `http://localhost:8080/api/appointments/booked-by-patient`;

    const payload = JSON.stringify({
        id: appointmentId,
        patientId: patientId,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${patientToken}`,
        },
    };

    const response = http.patch(url, payload, params);

    check(response, {
        'appointment booked': (r) => r.status === 200,
    });
}


export default function () {

    clinicId = createClinic();
    sleep(1)

    registerDentist(clinicId);
    sleep(1);


    dentistToken = loginDentist(dentistCredentials);
    sleep(1)


    let appointmentId = createAppointment(dentistToken);
    sleep(1)

    let patientCredentials = registerPatient();
    sleep(1);

    const {token, id} = loginPatient(patientCredentials);
    sleep(1);

    bookAppointment(id, token, appointmentId);
    sleep(1);

}
