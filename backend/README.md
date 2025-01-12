## Backend

The SmileSelect backend consists of a number of microservices.

- [**service-registry**](): registry of service instances
- [**appointment-service**](): service handling appointments
- [**auth-service**](): service that authenticates users with [JWT Authentication](https://jwt.io/)
- [**dentist-service**](): service handling dentist operations
- [**gateway-service**](): service routing client requests to service instances
- [**logging-service**](): service logging errors and events
- [**monitoring-service**](): service monitoring current stress on the system
- [**notification-service**](): service handling email notifications to users
- [**patient-service**](): service handling patient operations

## Requirements

- [Java v.21](https://www.oracle.com/se/java/technologies/downloads/#java21)
- [Apache Maven 4.0.0](https://maven.apache.org/download.cgi)
- [PostgreSQL 17](https://www.postgresql.org/)
- [Mosquitto MQTT](https://mosquitto.org/)
- [Docker](https://docs.docker.com/get-started/get-docker/)
- [MongoDB](https://www.mongodb.com/)

## Usage 

To successfully run the backend. Start with [service-registry instructions]() and continue with each subsequent service.


