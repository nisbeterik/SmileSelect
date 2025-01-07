# Running stress-test

## Instructions

These instructions needs to be followed in the exact order as instructed

### Install k6

For the **stress-testing** to work, a **global** installation of `k6` is
required. The instructions can be found
[here](https://k6.io/docs/get-started/installation/).

### Start service registry
Open a new terminal in the project root directory and run the following commands

```bash
cd backend/registry/service-registry
mvn spring-boot:run
```
This will start the service-registry. *DO NOT CLOSE THE TERMINAL*

### Start patient-service
Open a new terminal in the project root directory and run the following commands

```bash
cd backend/services/patient-service 
```
In the patient-service registry, the 'test' profile needs to be activated.
Run this command:
#### Windows Command prompt
```bash
set SPRING_PROFILES_ACTIVE=test
echo %SPRING_PROFILES_ACTIVE% # To verify
```
#### PowerShell
```bash
$env:SPRING_PROFILES_ACTIVE = "test"
echo $env:SPRING_PROFILES_ACTIVE # To verify
```

#### macOS Terminal
```bash
export SPRING_PROFILES_ACTIVE="test"
echo $SPRING_PROFILES_ACTIVE # To verify
```

To switch back to the default profile, either open a new terminal session or run the same command but swap 'test' for 'prod'
Finally, run
```bash
mvn spring-boot:run
```
Now, patient-service is up and running. Do not close the terminal session until stress testing is done.

### Start gateway-service

Open a new terminal in the project root directory and run the following commands

```bash
cd backend/services/patient-service 
```

**SAME AS PATIENT-SERVICE**

### Start auth-service
Open a new terminal in the project root and run the following commands:

```bash
cd backend/services/auth-service
mvn spring-boot:run
```
Do not close terminal until done with stress testing

### Start dentist-service

Open a new terminal in the project root directory and run the following commands

```bash
cd backend/services/dentist-service 
```

**SAME AS PATIENT-SERVICE**

### Start appointment-service

#### Run docker
Start docker desktop (if you don't have install it)
Open a new terminal in the project root directory
Run the following command: 
```bash
docker compose up
```
This will start the dockerized databases for appointment service.

#### Run the service itself
Open a new terminal in the root of the project directory.
Run the following commands:
```bash
cd backend/services/appointment-service
mvn spring-boot:run
```

## THIS CONCLUDES THE SETUP, TIME TO RUN THE STRESS-TEST

### Running the stress-test

Open a new terminal in the root of the project directory
Run the following commands:
```bash
cd backend/stress-test/
npm install
```

Now everything is ready to be run.
In the same terminal, run:

```bash
npm run normal:light
```
For the light normalScenario.js test
```bash
npm run normal:full
```
For the resource intensive full 3k user simulation
