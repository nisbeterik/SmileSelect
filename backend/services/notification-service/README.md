## notification-service

Before running notification-service, make sure all instructions and requirements in [backend](backend/README.md) have been followed.
Also make sure [service-registry](backend/registry/service-registry/README.md) is running.

### First time setup

#### setup env.properties
1. Before running the service a new file needs to be added in the registry folder
2. create file `env.properties` in `/backend/notification-service/src/main/resources`
3. add this to the file:
   ```
   MAIL_USERNAME=<replace with gmail address>
   MAIL_PASSWORD=<replace with password>
4. replace the fields with relevant email credentials

### Create a database in pgAdmin

- Open pgAdmin
- Create a new database called 'notificationDB' running on port 5432 with username: postgres password: postgres

### Usage
- Start the database server created in pgAdmin
- Open a new terminal in the root of the project
- Navigate to the directory where notification-service pom.xml is located ``cd backend/services/notification-service``
- Start the service by running ``mvn spring-boot:run``
- Verify that no errors occurred and that the service is running by observing the terminal console
