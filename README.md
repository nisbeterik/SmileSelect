# SmileSelect
<img src="assets/images/SmileSelect.jpg" alt="SmileSelect Logo" width="300" height="150">

<h3>Table of Content </h3>

- [SmileSelect](https://git.chalmers.se/courses/dit355/2024/student_teams/dit356_2024_01/dit356-group1#dit356-group1)
  * [Synopsis & Motivation](#synopsis-motivation)
  * [Dependencies & Requirements](#dependencies-requirements)
  * [Installation & Usage](#installation-usage)
  * [Software Architechture](#software-architechture)
  * [Authors & Acknowledgment](#authors-acknowledgment)


## <a id="synopsis-motivation"></a>Synopsis & Motivation 

SmileSelect is a web-based application designed to streamline the process of finding and booking dentist appointments for residents in Gothenburg. Given the high demand for dental care and limited availability at many clinics, patients often face a time-consuming process involving searches and phone calls with low success rates. SmileSelect simplifies this experience by offering a responsive interface where users can view available appointment slots on a navigable map, select their preferred time windows, and book or cancel appointments with ease. 

The application employs a robust distributed system architecture using MQTT-based middleware to manage, update, and communicate appointment availability in real time, ensuring that users and clinics stay informed. This setup enhances fault tolerance, maintains data accuracy, and allows for seamless updates, ultimately making the dental booking experience more efficient and accessible while supporting better dental care access across the community.

SmileSelect: Smile-tacular dental care, just book and prepare!

## <a id="dependencies-requirements"></a>Dependencies & Requirements

 
## <a id="installation-usage"></a>Installation & Usage


## <a id="software-architechture"></a>Software Architechture 

<details><summary>Component Diagram</summary>

![Component Diagram](assets/diagrams/component-diagram-milestone3.png)

*This component diagram represents a microservice-based architecture for a distributed dental management system. The system is made up of multiple microservices, a central API Gateway, and an MQTT broker for asynchronous communication between services. Some details of the architecture are as follows:*

**User Interfaces:**

The system has two frontends: Patient UI and Dentist UI, which interact with the backend via the API Gateway using RESTful HTTP communication. 

**API Gateway:**

Acts as a single entry point for external communication, forwarding user requests to the respective microservices (i.e. Appointment-Service, Dentist-Service).

**Microservices:**

Each microservice is designed for a specific functionality:

* Appointment-Service: Manages appointments.
* Auth-Service: Handles authentication and authorization.
* Dentist-Service: Manages dentist-related data.
* Logging-Service: Handles logging operations for monitoring of events.
* Notification-Service: Sends notifications via MQTT.
* Patient-Service: Manages patient-related data.



**Databases**:

Each microservice (except Auth-Service) has a dedicated database to store its data, ensuring modularity and scalability.

**Communication**:

* Synchronous communication (REST) occurs between the API Gateway and microservices.
* Asynchronous communication (MQTT) is used for inter-service messaging, improving decoupling and scalability.


</details>

<details><summary>Entity-Relationship (ER) Diagram</summary>

![Entity-Relationship Diagram](assets/diagrams/er-diagram.png)

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

## <a id="authors-acknowledgment"></a>Authors & Acknowledgment

* **Erik Nisbet** (@eriknis)

* **Edvin Sanfridsson** (@edvsan)

* **Fredrik Nilsson** (@fnilsson)

* **Love Carlander Strandäng** (@loveca)

* **Martin Lidgren** (@marlidg)



    -------------------------------------------------------

    _SmileSelect - DIT356 H24 - University of Gothenburg, Sweden_
