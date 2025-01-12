# Monitoring Service

The **monitoring-service** is a microservice within the SmileSelect backend that monitors system performance, tracks current stress levels, and provides real-time insights for system health and scalability.

## Table of Contents
- [Requirements](#requirements)
- [Usage](#usage)

---

## <a id="requirements"></a>Requirements

Before running the `monitoring-service`, ensure the following dependencies are installed and set up:
- [Java v.21](https://www.oracle.com/se/java/technologies/downloads/#java21)
- [Apache Maven 4.0.0](https://maven.apache.org/download.cgi)
- [PostgreSQL 17](https://www.postgresql.org/)
- [Mosquitto MQTT](https://mosquitto.org/)

Ensure that the **[service-registry](../registry/service-registry/README.md)** is running before starting the monitoring service.

---

## <a id="usage"></a>Usage

- Open a new terminal in the root of the project
- Navigate to the directory where auth-service pom.xml is located ``cd backend/services/monitoring-service``
- Start the service by running ``mvn spring-boot:run``
- Verify that no errors occurred and that the service is running by observing the terminal console