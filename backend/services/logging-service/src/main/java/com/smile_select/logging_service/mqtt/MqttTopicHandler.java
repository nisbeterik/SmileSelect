package com.smile_select.logging_service.mqtt;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.smile_select.logging_service.service.LogService;
import org.springframework.integration.mqtt.support.MqttHeaders;

@Component
public class MqttTopicHandler {

    private final LogService logService;

    public MqttTopicHandler(LogService logService) {
        this.logService = logService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(
        Message<String> message, 
        @Header(MqttHeaders.RECEIVED_TOPIC) String topic
    ) {
        switch (topic) {
            case "/topic":
                // Handle incoming message with specified topic by calling methods in logService
                System.out.println("Incoming message from topic: " + topic);
                System.err.println("Payload: " + message.getPayload());
                break;

    }

    }
}


