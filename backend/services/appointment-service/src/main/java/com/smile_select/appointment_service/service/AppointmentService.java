package com.smile_select.appointment_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    
    @Autowired
	MqttGateway mqttGateway;

    // ObjectMapper set up to handle LocalDateTime, which it does not by default
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()
                    .addSerializer(LocalDateTime.class,
                            new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
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

    public void publishMessage(String topic, Appointment appointment) {
        try {
            String message = objectMapper.writeValueAsString(appointment);
            mqttGateway.publishMessage(message, topic);
            System.out.println("Published message to topic: " + topic);
        } catch (Exception e) {
            System.err.println("Failed to publish message to topic " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
