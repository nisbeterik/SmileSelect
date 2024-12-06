package com.smile_select.appointment_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.smile_select.appointment_service.model.Appointment;
import com.smile_select.appointment_service.mqtt.MqttGateway;
import com.smile_select.appointment_service.repository.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final MqttGateway mqttGateway;
    private final ObjectMapper objectMapper;
    // Import Jdbc Primary/Fallback templates
    private final JdbcTemplate primaryJdbcTemplate;
    private final JdbcTemplate fallbackJdbcTemplate;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              MqttGateway mqttGateway,
                              @Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate,
                              @Qualifier("fallbackJdbcTemplate") JdbcTemplate fallbackJdbcTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.mqttGateway = mqttGateway;
        this.primaryJdbcTemplate = primaryJdbcTemplate;
        this.fallbackJdbcTemplate = fallbackJdbcTemplate;

        // Configure the ObjectMapper
        this.objectMapper = new ObjectMapper();
        // Register the JavaTimeModule to handle Java 8 Date/Time types
        this.objectMapper.registerModule(new JavaTimeModule());
        // Disable writing dates as timestamps
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    // Fallback methods

    // Health check
    private boolean isPrimaryHealthy() {
        try {
            primaryJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<Appointment> queryFallbackDB(String sql, Object... args) {
        return fallbackJdbcTemplate.query(sql, args, (rs, rowNum) -> {
            Appointment appt = new Appointment();
            appt.setId(rs.getLong("id"));
            appt.setPatientId(rs.getLong("patient_id"));
            appt.setDentistId(rs.getLong("dentist_id"));
            appt.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            appt.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
            return appt;
        });
    }


    // Appointment methods
    // Fallback alternative included for most methods

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findAll();
        } else {
            System.out.println("Primary DB down. Fetching all appointments from fallback DB.");
            return queryFallbackDB("SELECT * FROM appointment");
        }
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findById(id);
        } else {
            System.out.println("Primary DB down. Fetching appointment by ID from fallback DB.");
            List<Appointment> results = queryFallbackDB("SELECT * FROM appointment WHERE id = ?", id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
    }


    public List<Appointment> getAppointmentsAfterDate(LocalDate startDate) {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findByStartTimeDateAfter(startDate);
        } else {
            System.out.println("Primary DB down. Fetching from fallback DB.");
            // Replicate the logic: DATE(start_time) > startDate
            return queryFallbackDB("SELECT * FROM appointment WHERE DATE(start_time) > ?", startDate);
        }
    }

    public List<Appointment> getAppointmentsBeforeDate(LocalDate endDate) {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findByStartTimeDateBefore(endDate);
        } else {
            System.out.println("Primary DB down. Fetching from fallback DB.");
            return queryFallbackDB("SELECT * FROM appointment WHERE DATE(start_time) < ?", endDate);
        }
    }

    public List<Appointment> getAppointmentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findByStartTimeDateBetween(startDate, endDate);
        } else {
            System.out.println("Primary DB down. Fetching from fallback DB.");
            return queryFallbackDB("SELECT * FROM appointment WHERE DATE(start_time) BETWEEN ? AND ?", startDate, endDate);
        }
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsByDentistId(Long dentistId) {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findByDentistId(dentistId);
        } else {
            System.out.println("Primary DB down. Fetching from fallback DB.");
            return queryFallbackDB("SELECT * FROM appointment WHERE dentist_id = ?", dentistId);
        }
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findByPatientId(patientId);
        } else {
            System.out.println("Primary DB down. Fetching from fallback DB.");
            return queryFallbackDB("SELECT * FROM appointment WHERE patient_id = ?", patientId);
        }
    }

    public List<Appointment> getAvailableAppointmentsByDentistId(Long dentistId) {
        if (isPrimaryHealthy()) {
            return appointmentRepository.findAvailableAppointmentsByDentistId(dentistId);
        } else {
            System.out.println("Primary DB down. Fetching from fallback DB.");
            return queryFallbackDB("SELECT * FROM appointment WHERE dentist_id = ? AND patient_id IS NULL", dentistId);
        }
    }

    // Method for publishing an MQTT message containting a stringified appointment JSON-object
    public void publishAppointmentMessage(String topic, Appointment appointment) {
        try {
            String message = objectMapper.writeValueAsString(appointment);
            mqttGateway.publishMessage(message, topic);
            System.out.println("Published message to topic: " + topic);
        } catch (Exception e) {
            System.err.println("Failed to publish message to topic " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void publishAppointmentCreatedEvent(Appointment appointment) {
        try {
            // Use the class-level objectMapper
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointment.getId());
            messageMap.put("patientId", appointment.getPatientId());
            messageMap.put("startTime", appointment.getStartTime()); // LocalDateTime

            String message = objectMapper.writeValueAsString(messageMap);
            System.out.println("Message being published: " + message);
            mqttGateway.publishMessage(message, "/appointments/booked");
            System.out.println("Published appointment created event to topic: /appointments/booked");
        } catch (Exception e) {
            System.err.println("Failed to publish appointment created event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
