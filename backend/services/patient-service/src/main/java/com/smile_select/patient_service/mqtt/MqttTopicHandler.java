package com.smile_select.patient_service.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.smile_select.patient_service.service.PatientService;
import org.springframework.integration.mqtt.support.MqttHeaders;

@Component
public class MqttTopicHandler {

    private final PatientService patientService;

    @Autowired
    private MqttGateway mqttGateway;

    public MqttTopicHandler(PatientService patientService) {
        this.patientService = patientService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(
            Message<String> message,
            @Header(MqttHeaders.RECEIVED_TOPIC) String topic
    ) {
        switch (topic) {
            case "/auth/login-patient/request":
                patientService.handlePatientLoginRequest(message.getPayload());
                break;

            case "/appointments/created":
                patientService.processAppointmentCreated(message.getPayload());
                break;

            case "/appointments/cancelled-by-dentist":
                patientService.handleAppointmentCancellation(message.getPayload());
                break;

            case "/appointments/booked":
                patientService.processAppointmentBooked(message.getPayload());
                break;
        }
    }
}



