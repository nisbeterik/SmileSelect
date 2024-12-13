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
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

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
    public void publishMessage(String payload, String topic) {
        mqttGateway.publishMessage(payload, topic);
    }

    public void sendEmail(String to, String subject, String content) {
        emailService.sendEmail(to, subject, content);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void processNewAppointmentSlot(String payload) {
        System.out.println("Received message for new appointment slot posted");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            JsonNode rootNode = objectMapper.readTree(payload);

            String startTime = rootNode.path("startTime").asText();
            String appointmentId = rootNode.path("appointmentId").asText();

            // Convert startTime to LocalDateTime
            LocalDateTime formatedTime = objectMapper.convertValue(startTime, LocalDateTime.class);

            // Format startTime to a nicer format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedStartTime = formatedTime.format(formatter);

            // Extract the list of patients (with emails and first names) from the payload
            Iterator<JsonNode> patientsIterator = rootNode.path("emailsAndNamesList").elements();
            
            while (patientsIterator.hasNext()) {
                JsonNode patientNode = patientsIterator.next();

                // Extract email and first name 
                String email = patientNode.path("email").asText(); 
                String firstName = patientNode.path("firstName").asText(); 

                // Prepare email content
                String subject = "New Appointment Slot Available";
                String content = "Dear " + firstName + "\n\n" +
                        "A new open appointment slot has been posted for: " + formattedStartTime + "\n"
                        + "Appointment ID: " + appointmentId + "\n"
                        + "Please check your availability.\n\n" +
                        "Best regards,\n" +
                        "SmileSelect Team";

                // Send email asynchronously
                emailService.sendEmail(email, subject, content);

                // Save the notification (optional)
                Notification notification = new Notification();
                notification.setEmail(email);
                notification.setTime(LocalDateTime.now());
                notification.setMessage(content);
                save(notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void processAppointmentCancellationByPatient(String payload) {
        System.out.println("Received message for patient cancellation");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            JsonNode rootNode = objectMapper.readTree(payload);

            String dentistEmail = rootNode.path("dentistEmail").asText();
            String dentistFirstName = rootNode.path("dentistFirstName").asText();
            String appointmentStartTime = rootNode.path("appointmentStartTime").asText();
            String appointmentId = rootNode.path("appointmentId").asText();

            // Convert startTime to LocalDateTime
            LocalDateTime formatedTime = objectMapper.convertValue(appointmentStartTime, LocalDateTime.class);

            // Format startTime to a nicer format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedStartTime = formatedTime.format(formatter);

            // Prepare email content
            String subject = "Patient has cancelled appointment";
            String content = "Dear " + dentistFirstName + "\n\n"
                    + "A patient you had scheduled has cancelled their appointment.\n"
                    + "Appointment ID: " + appointmentId + "\n"
                    + "Start Time: " + formattedStartTime + "\n\n"
                    + "Please contact us if you have any questions.\n\n"
                    + "Best regards,\n"
                    + "SmileSelect Team";

            // Send email
            sendEmail(dentistEmail, subject, content);

            // Save the notification (optional)
            Notification notification = new Notification();
            notification.setEmail(dentistEmail);
            notification.setTime(LocalDateTime.now());
            notification.setMessage(content);
            save(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processAppointmentCancellationByDentist(String payload) {
        System.out.println("Received message for dentist cancellation");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            JsonNode rootNode = objectMapper.readTree(payload);

            String patientEmail = rootNode.path("patientEmail").asText();
            String patientFirstName = rootNode.path("patientFirstName").asText();
            String appointmentStartTime = rootNode.path("appointmentStartTime").asText();
            String appointmentId = rootNode.path("appointmentId").asText();

            // Convert startTime to LocalDateTime
            LocalDateTime formatedTime = objectMapper.convertValue(appointmentStartTime, LocalDateTime.class);

            // Format startTime to a nicer format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedStartTime = formatedTime.format(formatter);

            // Prepare email content
            String subject = "Your appointment has been cancelled";
            String content = "Dear " + patientFirstName + "\n\n"
                    + "Your dentist has cancelled your appointment.\n"
                    + "Appointment ID: " + appointmentId + "\n"
                    + "Start Time: " + formattedStartTime + "\n\n"
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

            // Convert startTime to LocalDateTime
            LocalDateTime formatedTime = objectMapper.convertValue(startTime, LocalDateTime.class);

            // Format startTime to a nicer format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedStartTime = formatedTime.format(formatter);

            System.out.println("appointmentId: " + appointmentId);
            System.out.println("patientId: " + patientId);
            System.out.println("patientEmail: " + patientEmail);
            System.out.println("patientFirstName: " + patientFirstName);
            System.out.println("startTime: " + startTime);

            if (patientEmail == null || patientEmail.isEmpty()) {
                System.out.println("Patient email is missing in the payload.");
                return;
            }

            // Prepare email content
            String subject = "Your New Appointment is Scheduled";
            String content = "Dear " + patientFirstName + "\n\n"
                    + "Your dentist has scheduled a new appointment for you.\n"
                    + "Appointment ID: " + appointmentId + "\n"
                    + "Start Time: " + formattedStartTime + "\n\n"
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

    // Send Email-notification to patient when booked via DENTIST
    public void processAppointmentWithEmailByDentist(String payload) {
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

            // Convert startTime to LocalDateTime
            LocalDateTime formatedTime = objectMapper.convertValue(startTime, LocalDateTime.class);

            // Format startTime to a nicer format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedStartTime = formatedTime.format(formatter);

            System.out.println("appointmentId: " + appointmentId);
            System.out.println("patientId: " + patientId);
            System.out.println("patientEmail: " + patientEmail);
            System.out.println("patientFirstName: " + patientFirstName);
            System.out.println("startTime: " + startTime);

            if (patientEmail == null || patientEmail.isEmpty()) {
                System.out.println("Patient email is missing in the payload.");
                return;
            }

            // Prepare email content
            String subject = "Your New Appointment is Scheduled";
            String content = "Dear " + patientFirstName + "\n\n"
                    + "Your dentist has scheduled a new appointment for you.\n"
                    + "Appointment ID: " + appointmentId + "\n"
                    + "Start Time: " + formattedStartTime + "\n\n"
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

    // Send Email-notification to patient when booked via availability slot
    public void processAppointmentWithEmailBookedByPatient(String payload) {
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

            // Convert startTime to LocalDateTime
            LocalDateTime formatedTime = objectMapper.convertValue(startTime, LocalDateTime.class);

            // Format startTime to a nicer format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedStartTime = formatedTime.format(formatter);

            System.out.println("appointmentId: " + appointmentId);
            System.out.println("patientId: " + patientId);
            System.out.println("patientEmail: " + patientEmail);
            System.out.println("patientFirstName: " + patientFirstName);
            System.out.println("startTime: " + startTime);

            if (patientEmail == null || patientEmail.isEmpty()) {
                System.out.println("Patient email is missing in the payload.");
                return;
            }

            // Prepare email content
            String subject = "Your New Appointment is Scheduled";
            String content = "Dear " + patientFirstName + "\n\n"
                    + "Your new booking has been confirmed! See following information: \n"
                    + "Appointment ID: " + appointmentId + "\n"
                    + "Start Time: " + formattedStartTime + "\n\n"
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
