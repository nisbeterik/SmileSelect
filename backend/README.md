## Backend

The SmileSelect backend consists of a number of microservices.

- [**service-registry**](backend/registry/service-registry/README.md): registry of service instances
- [**appointment-service**](backend/services/appointment-service/README.md): service handling appointments
- [**auth-service**](backend/services/auth-service/README.md): service that authenticates users with [JWT Authentication](https://jwt.io/)
- [**dentist-service**](backend/services/dentist-service/README.md): service handling dentist operations
- [**gateway-service**](backend/services/gateway-service/README.md): service routing client requests to service instances
- [**logging-service**](backend/services/logging-service/README.md): service logging errors and events
- [**monitoring-service**](backend/services/monitoring-service/README.md): service monitoring current stress on the system
- [**notification-service**](backend/services/notification-service/README.md): service handling email notifications to users
- [**patient-service**](backend/services/patient-service/README.md): service handling patient operations

## Requirements

- [Java v.21](https://www.oracle.com/se/java/technologies/downloads/#java21)
- [Apache Maven 4.0.0](https://maven.apache.org/download.cgi)
- [PostgreSQL 17](https://www.postgresql.org/)
- [Mosquitto MQTT](https://mosquitto.org/)
- [Docker](https://docs.docker.com/get-started/get-docker/)
- [MongoDB](https://www.mongodb.com/)

## Usage 

To successfully run the backend. Start with [service-registry instructions](), then continue with this document. After finishing setting up what is outlined here,
continue with each subsequent service linked above.

### Mosquitto broker

#### Install Mosquitto

**Windows**

- Go to https://mosquitto.org/download/ and download the binary.
- Select run as a service in installation options
- Verify the installation by running  ```mosquitto -v```
- Verify that its running as a service with ```Win + R``` and typing ```services.msc```
- Look for Mosquitto in the list
- Verify that mosquitto is running on ```port 1883```

**macOS**

- Install with ``brew install mosquitto``
- Run as a service
- Verify that mosquitto runs on port 1883

### PostgreSQL

- [Download PostgreSQL](https://www.postgresql.org/download/)
- Install for your operating system
- Make sure pgAdmin is installed

### Docker

- [Download and install Docker Desktop](https://docs.docker.com/desktop/)

### Java 21

- [Download and install Java 21](https://www.oracle.com/se/java/technologies/downloads/#java21)

### Maven 4.0.0
- [Download and install Apache Maven 4.0.0](https://maven.apache.org/download.cgi)

### Architecture

<details><summary>Component Diagram</summary>

![Component Diagram](assets/diagrams/component-diagram-milestone4.png)

*This component diagram represents a microservice-based architecture for a distributed dental management system. The system is made up of multiple microservices, a central API Gateway access point, a Service Registry for service discovery, and an MQTT broker for asynchronous communication between services. Some details of the architecture are as follows:*

**User Interfaces:**

The system has two frontends: Patient UI and Dentist UI, which interact with the backend via the API Gateway using RESTful HTTP communication.

**API Gateway:**

Acts as a single entry point for client requests and communication, forwarding user requests to the respective microservices (i.e. Appointment-Service, Dentist-Service).

**Service Registry**

The Service Registry (Eureka Server) is integrated to manage and maintain a dynamic registry of all running microservices. All microservices register themselves at the registry, enabling dynamic service discovery and removing the need for hardcoded endpoints.

**Microservices:**

Each microservice is designed for a specific functionality:

* Appointment-Service: Manages appointments.
* Auth-Service: Handles authentication and authorization.
* Dentist-Service: Manages dentist-related data.
* Logging-Service: Handles logging operations for monitoring of events.
* Monitoring-Service: Monitors all events in the system and provides real-time insight for health and performance.
* Notification-Service: Sends notifications via MQTT.
* Patient-Service: Manages patient-related data.



**Databases**:

Each microservice (except Auth-Service and Monitoring-Service) has dedicated databases to store its data, ensuring modularity and scalability.

**Communication**:

* Synchronous communication (REST) occurs between the API Gateway and microservices.
* Asynchronous communication (MQTT) is used for inter-service messaging, improving decoupling and scalability.


</details>

<details><summary>Entity-Relationship (ER) Diagram</summary>

![Entity-Relationship Diagram](assets/diagrams/er-diagram-milestone4.png)

*This ER diagram represents and outlines the structure of a dental management system, capturing the core relationships and data flows between clinics, dentists, patients, appointments, and notifications.*
*It models the relationships between these entities and their respective roles in the system.*

</details>

<details><summary>Deployment Diagram</summary>

![Deployment Diagram](assets/diagrams/deployment-diagram-milestone4.png)

This Deployment Diagram represents a distributed system architecture of SmileSelect, detailing the physical deployment of various components and their interactions:

**API Gateway:**

Serves as the central entry point for managing all incoming HTTP requests from the WebClient, routing them to appropriate backend services.

**WebClient:**

Represents the user-facing frontend, hosted on a web server and communicating with the backend via the API Gateway.

**Backend Services:** Includes modular services, each deployed on Spring Boot servers:

* Dental Service, Logging Service, Patient Service, and Notification Service for domain-specific operations.
* Appointment Service deployed with primary and redundant database configurations for fault tolerance.

**Databases:**

Each service has its own dedicated PostgreSQL database (I.e. DentalDB, PatientDB, NotificationDB) for managing application data and data storage.

**MQTT Broker:**

Enables real-time communication using a publish/subscribe mechanism for asynchronous event handling.

**Authentication Service:**

A dedicated service managing user authentication and security.

**Communication:**

* Most interactions between nodes occur over HTTP
* Publish/Subscribe messaging is used inter-service for asynchronous communication.

</details>

<details><summary>Development View</summary>

![Development-View](assets/diagrams/development-view-diagram.png)

*This Development View Diagram outlines a high-level structure of any given microservice. It shows the general layered architecture of the system's microservices with a controller-service-repository pattern.*

</details>