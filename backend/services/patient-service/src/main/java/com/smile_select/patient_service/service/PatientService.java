package com.smile_select.patient_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.smile_select.patient_service.dto.PatientFirstNameAndEmailDTO;
import com.smile_select.patient_service.dto.PatientUpdateDTO;
import com.smile_select.patient_service.exception.ResourceNotFoundException;
import com.smile_select.patient_service.model.Patient;
import com.smile_select.patient_service.model.PatientPreferredDate;
import com.smile_select.patient_service.mqtt.MqttGateway;
import com.smile_select.patient_service.repository.PatientPreferredDateRepository;
import com.smile_select.patient_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.persistence.criteria.Predicate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class PatientService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientPreferredDateRepository patientPreferredDateRepository;

    // ObjectMapper with support for LocalDate and LocalDateTime
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()
                    .addSerializer(LocalDateTime.class,
                            new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .addSerializer(LocalDate.class,
                            new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE)))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public Optional<Patient> findPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public PatientPreferredDate savePatientPreferredDate(PatientPreferredDate preferredDate) {
        return patientPreferredDateRepository.save(preferredDate);
    }

    public boolean checkPassword(Patient patient, String rawPassword) {
        return passwordEncoder.matches(rawPassword, patient.getPassword());
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null.");
        }

        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
    }

    public Patient getPatientByIdAndEmail(Long id, String userEmail) {
        if (id == null) {
            throw new ResourceNotFoundException("Patient ID must not be null");
        }
        return patientRepository.findById(id)
                .filter(patient -> patient.getEmail().equals(userEmail)) // Ensure only the owner can access their data
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found or access denied for ID: " + id));
    }
    public Patient getPatientByIdAsDentist(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found or access denied for ID: " + id));
    }

    // Delete a patient
    public void deletePatientById(Long id, String userEmail) {
        Patient patient = getPatientByIdAndEmail(id, userEmail);
        patientRepository.delete(patient);
    }

    // Update patient details
    public void updatePatientDetails(Long id, String userEmail, PatientUpdateDTO updateDetails) {
        Patient patient = getPatientByIdAndEmail(id, userEmail);

        if (updateDetails.getFirstName() != null) {
            patient.setFirstName(updateDetails.getFirstName());
        }
        if (updateDetails.getLastName() != null) {
            patient.setLastName(updateDetails.getLastName());
        }
        if (updateDetails.getEmail() != null) {
            patient.setEmail(updateDetails.getEmail());
        }
        if (updateDetails.getDateOfBirth() != null) {
            patient.setDateOfBirth(updateDetails.getDateOfBirth());
        }
        patientRepository.save(patient);
    }

    // find a patient from partial information
    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        final String finalSearchQuery = query.trim().toLowerCase();

        return patientRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            System.out.println(predicates + "innan");
            try {
                Long id = Long.parseLong(finalSearchQuery);
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            } catch (NumberFormatException error) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like( // checks email
                                criteriaBuilder.lower(root.get("email")),
                                "%" + finalSearchQuery + "%"),
                        criteriaBuilder.like( // checks first name
                                criteriaBuilder.lower(root.get("first_name")),
                                "%" + finalSearchQuery + "%"),
                        criteriaBuilder.like( // checks last name
                                criteriaBuilder.lower(root.get("last_name")),
                                "%" + finalSearchQuery + "%")));
            }
            System.out.println(predicates + "Efter");
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }

    public void handlePatientLoginRequest(String payload) {

        try {
            // Parse loginRequest payload
            JsonNode jsonNode = objectMapper.readTree(payload);

            // Extract correlationId and email
            String correlationId = jsonNode.get("correlationId").asText();
            String email = jsonNode.get("email").asText();

            System.out.println("Received login request for patient with email: " + email);

            // Find patient with extracted email
            Optional<Patient> patient = patientRepository.findByEmail(email);

            // Update topic for response to include correlationId
            String topic = "/auth/login-patient/response" + "/" + correlationId;

            String responsePayload;
            if (patient.isPresent()) {

                // Convert patient object to JSON string
                responsePayload = objectMapper.writeValueAsString(patient.get());
            } else {

                // Return empty JSON string if patient does not exist
                responsePayload = "{}";
            }

            // Publish response to MQTT
            System.out.println("Publishing login response under topic: " + topic);
            mqttGateway.publishMessage(responsePayload, topic);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Finding Patient id for email notification function
    public Patient getPatientByIdForEmail(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
    }

    public void processAppointmentBooked(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);

            Long appointmentId = rootNode.path("id").asLong();
            Long patientId = rootNode.path("patientId").asLong();
            String startTime = rootNode.path("startTime").asText();

            // Fetch patient email
            System.out.println("Fetching email for patientId: " + patientId);
            Patient patient = getPatientById(patientId);

            System.out.println("Retrieved patientEmail: " + patient.getEmail());

            // Prepare message to publish
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointmentId);
            messageMap.put("patientId", patientId);
            messageMap.put("patientEmail", patient.getEmail());
            messageMap.put("patientFirstName", patient.getFirstName());
            messageMap.put("startTime", startTime);

            String messageToPublish = objectMapper.writeValueAsString(messageMap);

            // Publish to the topic
            mqttGateway.publishMessage(messageToPublish, "/notifications/booked");
            System.out.println("Published message to /notifications/booked");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processAppointmentBookedByDentist(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);

            Long appointmentId = rootNode.path("id").asLong();
            Long patientId = rootNode.path("patientId").asLong();
            String startTime = rootNode.path("startTime").asText();

            // Fetch patient email
            System.out.println("Fetching email for patientId: " + patientId);
            Patient patient = getPatientById(patientId);

            System.out.println("Retrieved patientEmail: " + patient.getEmail());

            // Prepare message to publish
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointmentId);
            messageMap.put("patientId", patientId);
            messageMap.put("patientEmail", patient.getEmail());
            messageMap.put("patientFirstName", patient.getFirstName());
            messageMap.put("startTime", startTime);

            String messageToPublish = objectMapper.writeValueAsString(messageMap);

            // Publish to the topic
            mqttGateway.publishMessage(messageToPublish, "/notifications/booked-by-dentist");
            System.out.println("Published message to /notifications/booked-by-dentist");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processAppointmentBookedByPatient(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);

            Long appointmentId = rootNode.path("id").asLong();
            Long patientId = rootNode.path("patientId").asLong();
            String startTime = rootNode.path("startTime").asText();

            // Fetch patient email
            System.out.println("Fetching email for patientId: " + patientId);
            Patient patient = getPatientById(patientId);

            System.out.println("Retrieved patientEmail: " + patient.getEmail());

            // Prepare message to publish
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointmentId);
            messageMap.put("patientId", patientId);
            messageMap.put("patientEmail", patient.getEmail());
            messageMap.put("patientFirstName", patient.getFirstName());
            messageMap.put("startTime", startTime);

            String messageToPublish = objectMapper.writeValueAsString(messageMap);

            // Publish to the topic
            mqttGateway.publishMessage(messageToPublish, "/notifications/booked-by-patient");
            System.out.println("Published message to /notifications/booked-by-patient");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processAppointmentCreated(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);

            Long appointmentId = rootNode.path("id").asLong();
            String startTime = rootNode.path("startTime").asText();

            // Prepare message to publish
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointmentId);
            messageMap.put("startTime", startTime);

            // Finf all patients with a preferred date equaling start time date
            
            LocalDateTime localDateTime = LocalDateTime.parse(startTime);
            LocalDate localDate = localDateTime.toLocalDate();

            // Get a list of emails for the patients who have entered that preferred daye
            List<PatientFirstNameAndEmailDTO> emailsAndNamesList = patientPreferredDateRepository.findEmailsAndFirstNamesByPreferredDate(localDate);
            
            messageMap.put("emailsAndNamesList", emailsAndNamesList);

            String messageToPublish = objectMapper.writeValueAsString(messageMap);

            // Publish to the topic
            mqttGateway.publishMessage(messageToPublish, "/notifications/created");
            System.out.println("Published message to /notifications/created");
            System.out.println(messageToPublish);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAppointmentCancellation(String payload) {
        System.out.println("Received message for dentist cancellation");
        try {
            // Parse appointment payload
            JsonNode jsonNode = objectMapper.readTree(payload);

            // Extract necessary information for notification
            String startTime = jsonNode.get("startTime").asText();
            Long patientId = jsonNode.get("patientId").asLong();
            Long appointmentId = jsonNode.get("id").asLong();

            // Find patient
            Optional<Patient> optionalPatient = patientRepository.findById(patientId);
            String messageToBePublished;
            String topic = "/notifications/cancelled-by-dentist";

            if (optionalPatient.isPresent()) {

                Patient patient = optionalPatient.get();

                // Create JSON-data to send to notification service

                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("patientFirstName", patient.getFirstName());
                messageMap.put("patientEmail", patient.getEmail());
                messageMap.put("appointmentStartTime", startTime);
                messageMap.put("appointmentId", appointmentId);

                // Publish cancellation to via MQTT to send notification
                messageToBePublished = objectMapper.writeValueAsString(messageMap);
                System.out.println("Publishing message: " + messageToBePublished);
                System.out.println("Topic: " + topic);

                mqttGateway.publishMessage(messageToBePublished, topic);

            } else {
                // Handle case where patient does not exist
                throw new Exception("Patient not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeOldPreferredDates(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate today = LocalDate.now();

        // Remove old dates from the preferred dates set
        patient.getPreferredDates().removeIf(date -> date.getPreferredDate().isBefore(today));
    }

    public PatientPreferredDate getPatientPreferredDateById(Long id){
        PatientPreferredDate preferredDate = patientPreferredDateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Preferred date not found"));
            return preferredDate;
    }

    public void deletePatientPreferredDate(Long id){
        PatientPreferredDate preferredDate = patientPreferredDateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Preferred date not found"));
        patientPreferredDateRepository.deleteById(preferredDate.getId());
    }
}
