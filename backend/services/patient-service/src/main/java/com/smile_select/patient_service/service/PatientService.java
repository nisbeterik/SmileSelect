package com.smile_select.patient_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.smile_select.patient_service.dto.PatientUpdateDTO;
import com.smile_select.patient_service.exception.ResourceNotFoundException;
import com.smile_select.patient_service.model.Patient;
import com.smile_select.patient_service.mqtt.MqttGateway;
import com.smile_select.patient_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class PatientService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private PatientRepository patientRepository;

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
        // Hash the password before saving a patient
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepository.save(patient);
    }

    public boolean checkPassword(Patient patient, String rawPassword) {
        return passwordEncoder.matches(rawPassword, patient.getPassword());
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Retrieve patient by ID
    /*
     * public Patient getPatientById(Long id, String userEmail) {
     * // Mock authentication
     * String authenticatedEmail = "love.strandang@gmail.com"; // Use a hardcoded
     * email to simulate an authenticated
     * // user
     * return patientRepository.findById(id)
     * .filter(patient -> patient.getEmail().equals(authenticatedEmail))
     * .orElseThrow(() -> new
     * ResourceNotFoundException("Patient not found or access denied for ID: " +
     * id));
     * }
     */

    public Patient getPatientById(Long id, String userEmail) {
        return patientRepository.findById(id)
                .filter(patient -> patient.getEmail().equals(userEmail)) // Ensure only the owner can access their data
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found or access denied for ID: " + id));
    }

    // Delete a patient
    public void deletePatientById(Long id, String userEmail) {
        Patient patient = getPatientById(id, userEmail);
        patientRepository.delete(patient);
    }

    // Update patient details
    public void updatePatientDetails(Long id, String userEmail, PatientUpdateDTO updateDetails) {
        Patient patient = getPatientById(id, userEmail);

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

    public void handlePatientLoginRequest(String payload){
        
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
}
