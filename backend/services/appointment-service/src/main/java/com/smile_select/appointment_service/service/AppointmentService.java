package com.smile_select.appointment_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, MqttGateway mqttGateway) {
        this.appointmentRepository = appointmentRepository;
        this.mqttGateway = mqttGateway;

        // Configure the ObjectMapper
        this.objectMapper = new ObjectMapper();
        // Register the JavaTimeModule to handle Java 8 Date/Time types
        this.objectMapper.registerModule(new JavaTimeModule());
        // Disable writing dates as timestamps
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsAfterDate(LocalDate startDate) {
        return appointmentRepository.findByStartTimeDateAfter(startDate);
    }

    public List<Appointment> getAppointmentsBeforeDate(LocalDate endDate) {
        return appointmentRepository.findByStartTimeDateBefore(endDate);
    }

    public List<Appointment> getAppointmentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByStartTimeDateBetween(startDate, endDate);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsByDentistId(Long dentistId) {
        return appointmentRepository.findByDentistId(dentistId);
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAvailableAppointmentsByDentistId(Long dentistId) {
        return appointmentRepository.findAvailableAppointmentsByDentistId(dentistId);
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
