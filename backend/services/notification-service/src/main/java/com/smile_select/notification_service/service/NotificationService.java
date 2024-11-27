package com.smile_select.notification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smile_select.notification_service.model.Notification;
import com.smile_select.notification_service.mqtt.MqttGateway;
import com.smile_select.notification_service.repository.NotificationRepository;

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

}
