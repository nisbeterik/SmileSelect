package com.smile_select.patient_service.mqtt;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.smile_select.patient_service.service.PatientService;
import org.springframework.integration.mqtt.support.MqttHeaders;

@Component
public class MqttTopicHandler {

    private final PatientService patientService;

    public MqttTopicHandler(PatientService patientService) {
        this.patientService = patientService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(
        Message<String> message, 
        @Header(MqttHeaders.RECEIVED_TOPIC) String topic
    ) {
        switch (topic) {
            case "/topic":
                // Handle incoming message with specified topic by calling methods in patientService
                System.out.println("Incoming message from topic: " + topic);
                System.err.println("Payload: " + message.getPayload());
                break;

    }

    }
}


