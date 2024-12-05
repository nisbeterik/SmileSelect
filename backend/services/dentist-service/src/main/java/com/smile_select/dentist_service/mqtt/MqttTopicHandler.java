package com.smile_select.dentist_service.mqtt;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.smile_select.dentist_service.service.DentistService;
import org.springframework.integration.mqtt.support.MqttHeaders;

@Component
public class MqttTopicHandler {

    private final DentistService dentistService;

    public MqttTopicHandler(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(
            Message<String> message,
            @Header(MqttHeaders.RECEIVED_TOPIC) String topic
    ) {
        switch (topic) {
            case "/auth/login-dentist/request":
                dentistService.handleDentistLoginRequest(message.getPayload());
                break;

            case "/appointments/cancelled-by-patient":
                dentistService.handleAppointmentCancellation(message.getPayload());
                break;

        }

    }
}


