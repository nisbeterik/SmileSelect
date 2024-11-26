package com.smile_select.notification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smile_select.notification_service.model.Notification;
import com.smile_select.notification_service.mqtt.MqttGateway;
import com.smile_select.notification_service.repository.NotificationRepository;

@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;

    @Autowired
    MqttGateway mqttGateway;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Method for publishing MQTT messages
    public void publishMessage(String payload, String topic){
        mqttGateway.publishMessage(payload, topic);
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
