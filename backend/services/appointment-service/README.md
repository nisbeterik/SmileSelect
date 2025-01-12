# Installation Instructions

## Prerequisites

1. **Docker**: Ensure Docker is installed and running on your machine.
2. **Java**: Install Java 11 or higher.
3. **Maven**: Install Maven for building and running the Spring Boot application.
4. 

---
### First time setup
 #### setup env.properties 
1. Before running the service a new file needs to be added in the registry folder
   2. create file `env.properties` in `/backend/appointment-service/src/main/resources`
   3. add this to the file:
      ```
      jwt.secret=<Replace-with-your-JWT-key>
      jwt.expiration=3600000
   4. replace the *jwt.sercret* with a unique randomly generated secret key 
   
## Setup Steps

### 1. Before startup
1. [make sure the service-registry is up and running before starting any other service. See instructions](backend/registry/service-registry/README.md)
    

### 2. Start Docker Containers

1. Navigate to the directory containing your `docker-compose.yml` file.
2. Run the following command to start the necessary services:
   ```bash
   docker-compose up -d

This will set up the required services like databases, MQTT broker, and any other dependencies.

the following databases for the partitions (Primary and Fallback) and the Counter will be created:
- Partition 0 (Primary and Fallback)
- Partition 1 (Primary and Fallback)
- Partition 2 (Primary and Fallback)
- Counter database

the provided SQL scripts will automatically run on docker initialization:</br>
**Partition 0**:
- `init_partition_0_primary.sql`
- `init_partition_0_fallback.sql`

**Partition 1**:
- `init_partition_1_primary.sql`
- `init_partition_1_fallback.sql`

**Partition 2**:
- `init_partition_2_primary.sql`
- `init_partition_2_fallback.sql`

**Counter**:</br>
- `init_counter.sql`


### 3. Start the Service

1. Open a new terminal window.
2. Navigate to the backend directory:
   ```bash
   cd /backend/appointment-service

3. Run the following Maven command to start the Spring Boot application:

    ```bash
    mvn spring-boot:run
   
### Verifying the Setup

The service should now be accessible at `http://localhost:8081` (gateway port: `8080`).

Check the logs in the terminal to confirm that the application has started successfully and is connected to the required databases and MQTT broker.

### 4. shutting down the Service

1. **Stop the Spring Boot Application**

   If you started the service using `mvn spring-boot:run`, terminate it by:

    - Pressing `Ctrl + C` in the terminal where the application is running.

   This will stop the application and release the port.

---

2. **Stop Docker Containers**

   Stop the running containers to ensure proper shutdown:

   ```bash
   docker-compose down

---

### Troubleshooting

If you encounter any issues, ensure the following:

1. Docker containers are running:

    ```bash
    docker ps

2. Correct environment variables are configured in the application or application.properties.
3. env.properties has correct information. 