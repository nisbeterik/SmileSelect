package com.smile_select.dentist_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.smile_select.dentist_service.exception.ResourceNotFoundException;
import com.smile_select.dentist_service.model.Clinic;
import com.smile_select.dentist_service.model.Dentist;
import com.smile_select.dentist_service.mqtt.MqttGateway;
import com.smile_select.dentist_service.repository.ClinicRepository;
import com.smile_select.dentist_service.repository.DentistRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Service class to manage Dentist entities.
 * Provides methods to perform operations such as finding, saving, updating, and
 * deleting dentist records.
 */
@Service
public class DentistService {

    @Autowired
    private DentistRepository dentistRepository;
    @Autowired
    private ClinicRepository clinicRepository;
    @Autowired
    private MqttGateway mqttGateway;

    // ObjectMapper with support for LocalDate and LocalDateTime
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()
                    .addSerializer(LocalDateTime.class,
                            new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .addSerializer(LocalDate.class,
                            new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE)))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Password Encrypter
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<Dentist> findDentistByEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        return dentistRepository.findByEmail(email);
    }

    public Dentist saveDentist(Dentist dentist) {
        return dentistRepository.save(dentist);
    }

    public boolean checkPassword(Dentist dentist, String rawPassword) {
        return passwordEncoder.matches(rawPassword, dentist.getPassword());
    }

    public List<Dentist> getAllDentists() {
        return dentistRepository.findAll();
    }

    public Dentist findById(Long id) {
        return dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + id));
    }

    public Dentist updateDentist(Long id, Dentist dentistUpdate) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + id));

        // Update basic information if provided
        if (dentistUpdate.getFirstName() != null) {
            dentist.setFirstName(dentistUpdate.getFirstName());
        }
        if (dentistUpdate.getLastName() != null) {
            dentist.setLastName(dentistUpdate.getLastName());
        }
        if (dentistUpdate.getEmail() != null) {
            dentist.setEmail(dentistUpdate.getEmail());
        }

        // Update password if provided
        if (dentistUpdate.getPassword() != null && !dentistUpdate.getPassword().isEmpty()) {
            dentist.setPassword(passwordEncoder.encode(dentistUpdate.getPassword()));
        }

        // Update clinic association if provided
        if (dentistUpdate.getClinicId() != null) {
            Clinic clinic = clinicRepository.findById(dentistUpdate.getClinicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Selected clinic does not exist"));
            dentist.setClinic(clinic);
        }

        return dentistRepository.save(dentist);
    }

    public void deleteDentistById(Long id, String userEmail) {
        Optional<Dentist> optionalDentist = dentistRepository.findById(id);

        if (optionalDentist.isEmpty()) {
            throw new ResourceNotFoundException("Dentist not found with ID: " + id);
        }

        Dentist dentist = optionalDentist.get();

        // Allow deletion if the user is the dentist
        if (!dentist.getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Access denied for deleting dentist ID: " + id);
        }

        dentistRepository.delete(dentist);
    }

    public void handleDentistLoginRequest(String payload) {
        try {
            // Parse loginRequest payload
            JsonNode jsonNode = objectMapper.readTree(payload);

            // Extract correlationId and email
            String correlationId = jsonNode.get("correlationId").asText();
            String email = jsonNode.get("email").asText();

            System.out.println("Received login request for dentist with email: " + email);

            // Find patient with extracted email
            Optional<Dentist> dentist = dentistRepository.findByEmail(email);

            // Update topic for response to include correlationId
            String topic = "/auth/login-dentist/response" + "/" + correlationId;

            String responsePayload;
            if (dentist.isPresent()) {

                // Convert dentist object to JSON string
                responsePayload = objectMapper.writeValueAsString(dentist.get());
            } else {

                // Return empty JSON string if dentist does not exist
                responsePayload = "{}";
            }

            // Publish response to MQTT
            System.out.println("Publishing login response under topic: " + topic);
            mqttGateway.publishMessage(responsePayload, topic);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleAppointmentCancellation(String payload) {
        System.out.println("Received message for patient cancellation");
        try {
            // Parse appointment payload
            JsonNode jsonNode = objectMapper.readTree(payload);

            // Extract necessary information for notification
            String startTime = jsonNode.get("startTime").asText();
            Long dentistId = jsonNode.get("dentistId").asLong();
            Long appointmentId = jsonNode.get("id").asLong();

            // Find patient
            Optional<Dentist> optionalDentist = dentistRepository.findById(dentistId);
            String messageToBePublished;
            String topic = "/notifications/cancelled-by-patient";

            if (optionalDentist.isPresent()) {

                Dentist dentist = optionalDentist.get();

                // Create JSON-data to send to notification service
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("dentistFirstName", dentist.getFirstName());
                messageMap.put("dentistEmail", dentist.getEmail());
                messageMap.put("appointmentStartTime", startTime);
                messageMap.put("appointmentId", appointmentId);

                // Publish cancellation to via MQTT to send notification
                messageToBePublished = objectMapper.writeValueAsString(messageMap);
                System.out.println("Publishing message: " + messageToBePublished);
                System.out.println("Topic: " + topic);

                mqttGateway.publishMessage(messageToBePublished, topic);

            } else {
                // Handle case where dentist does not exist
                throw new Exception("Patient not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
