## auth-service

To run auth-service make sure all requirements and installations from [backend](README.md) are done and that [service registry](backend/registry/service-registry/README.md) is running.

### First Time Setup

#### setup env.properties 
1. Before running the service a new file needs to be added in the registry folder
2. create file `env.properties` in `/backend/auth-service/src/main/resources`
3. add this to the file:
       ```
       jwt.secret=<Replace-with-your-JWT-key>
       jwt.expiration=3600000```

4. replace the *jwt.sercret* with a unique randomly generated secret key

### Usage
- Open a new terminal in the root of the project
- Navigate to the directory where auth-service pom.xml is located ``cd backend/services/auth-service``
- Start the service by running ``mvn spring-boot:run``
- Verify that no errors occured and that the service is running by observing the terminal console

