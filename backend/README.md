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