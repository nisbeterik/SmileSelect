package com.smile_select.notification_service.mqtt;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.smile_select.notification_service.service.NotificationService;

import org.springframework.integration.mqtt.support.MqttHeaders;

@Component
public class MqttTopicHandler {

    private final NotificationService notificationService;

    public MqttTopicHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(
            Message<String> message,
            @Header(MqttHeaders.RECEIVED_TOPIC) String topic
    ) {
        switch (topic) {
            case "/notifications/created":
                notificationService.processNewAppointmentSlot(message.getPayload());
                break;

            case "/notifications/cancelled-by-patient":
                notificationService.processAppointmentCancellationByPatient(message.getPayload());
                break;

            case "/notifications/cancelled-by-dentist":
                notificationService.processAppointmentCancellationByDentist(message.getPayload());
                break;

            case "/notifications/booked":
                notificationService.processAppointmentWithEmail(message.getPayload());
                break;

            case "/notifications/booked-by-patient":
                notificationService.processAppointmentWithEmailBookedByPatient(message.getPayload());
                break;
        }
    }

}
