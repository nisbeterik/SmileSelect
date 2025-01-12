# Appointment Service

## Overview

The **Appointment Service** is an API-driven microservice for managing the dental clinic appointments. It includes features such as scheduling, updating, querying, and canceling appointments, as well as integration with MQTT for real-time communication and notifications.

---

## Service Purpose

This service provides the following functionalities:

1. **Scheduling and Booking**: Allows dentists to create available appointment slots and patients to book those slots.
2. **Query and Management**: Supports querying appointments by various criteria such as dentist, clinic, or date range.
3. **Partitioned Database Design**: Ensures scalability by using partitioned databases for storing appointments.
4. **Real-time Notifications**: Integrates with MQTT to notify users of appointment changes (creation, booking, cancellation).

---

## Implementation Details

### Core Components

1. **AppointmentController**: The primary REST API controller, handling endpoints for CRUD operations on appointments.
    - **Key Endpoints**:
        - `POST /api/appointments`: Create a new appointment.
        - `GET /api/appointments`: Query appointments by date range or other criteria.
        - `PATCH /api/appointments`: Update or book an appointment.
        - `DELETE /api/appointments/{id}`: Delete an appointment.
    - Handles patient and dentist-specific requests for appointment details and availability.

2. **AppointmentService**: Encapsulates business logic for:
    - Saving, updating, and deleting appointments.
    - Partition-based storage and fallback to replica databases.
    - Publishing MQTT notifications for real-time updates.

3. **StatisticsController**: Provides metrics and insights:
    - `GET /api/appointments/stats/partitions`: Get partition usage statistics.
    - `GET /api/appointments/stats/counter`: Retrieve the current counter value.

4. **MQTT Integration**:
    - **MqttConfig**: Configures MQTT connections and message channels.
    - **MqttGateway**: Acts as an interface for publishing messages to MQTT topics.
    - **MqttTopicHandler**: Processes incoming MQTT messages and integrates with the `AppointmentService`.

5. **Security**:
    - **JwtAuthenticationFilter**: Validates JWT tokens in requests to ensure authenticated access and role-based authorization.

6. **Data Layer**:
    - **AppointmentRepository**: `DEPRICATED`
    - **CounterService**: Manages global counters and partition statistics using SQL queries.

---

## How the Service Works

### Workflow

1. **Appointment Creation**:
    - Dentists create time slots using `POST /api/appointments`.
    - The `AppointmentService` assigns an appointment ID, determines the appropriate database partition, and stores the appointment.

2. **Appointment Booking**:
    - Patients or dentists book slots using `PATCH /api/appointments/booked-by-*`.
    - MQTT notifications (`/appointments/booked`) are sent to notify relevant users.

3. **Querying Appointments**:
    - The API supports flexible query options, such as filtering by date, dentist, or clinic.
    - Results are aggregated from all database partitions to provide a unified view.

4. **Partitioned Storage**:
    - Appointments are distributed across three database partitions for scalability.
    - The service uses health checks to decide between primary and fallback databases.

5. **Real-Time Notifications**:
    - MQTT is used for publishing real-time updates on appointment events, such as creation, booking, and cancellation.

6. **Metrics and Monitoring**:
    - The `StatisticsController` provides insights into partition utilization and system counters.

### Error Handling

- Invalid inputs (e.g., missing dentist IDs, expired dates) result in `400 BAD REQUEST` responses.
- Unauthorized access without valid JWTs triggers `401 UNAUTHORIZED`.
- Not-found scenarios (e.g., nonexistent appointments) return `404 NOT FOUND`.

---

## Technologies Used

- **Spring Boot**: REST API development.
- **Hibernate/JPA**: ORM for database interactions.
- **MQTT**: Real-time message handling.
- **JWT**: Authentication and authorization.
- **Partitioned Databases**: Enhanced scalability and reliability.

---
