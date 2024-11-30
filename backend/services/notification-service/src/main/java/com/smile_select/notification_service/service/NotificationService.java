package com.smile_select.notification_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smile_select.notification_service.model.Notification;
import com.smile_select.notification_service.mqtt.MqttGateway;
import com.smile_select.notification_service.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Autowired
    MqttGateway mqttGateway;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    // Method for publishing MQTT messages
    public void publishMessage(String payload, String topic){
        mqttGateway.publishMessage(payload, topic);
    }

    public void sendEmail(String to, String subject, String content){
        emailService.sendEmail(to, subject, content);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void processNewAppointmentSlot(String payload) {
        System.out.println("New appointment slot posted: ");
        System.out.println(payload);
        // LOGIC HERE
    }

    public void processAppointmentCancellationByPatient(String payload) {
        System.out.println("Appointment cancelled by patient: ");
        System.out.println(payload);
        // LOGIC HERE
    }

    public void processAppointmentCancellationByDentist(String payload) {
        System.out.println("Appointment cancelled by dentist: ");
        System.out.println(payload);
        // LOGIC HERE
    }

    public void processAppointmentWithEmail(String payload) {
        System.out.println("Processing appointment with email: ");
        System.out.println(payload);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            JsonNode rootNode = objectMapper.readTree(payload);

            Long appointmentId = rootNode.path("appointmentId").asLong();
            Long patientId = rootNode.path("patientId").asLong();
            String patientEmail = rootNode.path("patientEmail").asText();
            String patientFirstName = rootNode.path("patientFirstName").asText();
            String startTime = rootNode.path("startTime").asText();

            if (patientEmail == null || patientEmail.isEmpty()) {
                System.out.println("Patient email is missing in the payload.");
                return;
            }

            // Prepare email content
            String subject = "Your New Appointment is Scheduled";
            String content = "Dear " + patientFirstName + "\n\n"
                    + "Your dentist has scheduled a new appointment for you.\n"
                    + "Appointment ID: " + appointmentId + "\n"
                    + "Start Time: " + startTime + "\n\n"
                    + "Please contact us if you have any questions.\n\n"
                    + "Best regards,\n"
                    + "SmileSelect Team";

            // Send email
            sendEmail(patientEmail, subject, content);

            // Save the notification (optional)
            Notification notification = new Notification();
            notification.setEmail(patientEmail);
            notification.setTime(LocalDateTime.now());
            notification.setMessage(content);
            save(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
