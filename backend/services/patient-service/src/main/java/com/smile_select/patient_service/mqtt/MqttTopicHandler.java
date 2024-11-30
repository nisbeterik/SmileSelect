package com.smile_select.patient_service.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile_select.patient_service.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.smile_select.patient_service.service.PatientService;
import org.springframework.integration.mqtt.support.MqttHeaders;

import java.util.HashMap;
import java.util.Map;

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
            case "/topic":
                // Handle incoming message with specified topic by calling methods in patientService
                System.out.println("Incoming message from topic: " + topic);
                System.err.println("Payload: " + message.getPayload());
                break;

            case "/appointments/created":
                processAppointmentCreated(message.getPayload());
        }

    }

    private void processAppointmentCreated(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);

            Long appointmentId = rootNode.path("appointmentId").asLong();
            Long patientId = rootNode.path("patientId").asLong();
            String startTime = rootNode.path("startTime").asText();

            // Fetch patient email
            System.out.println("Fetching email for patientId: " + patientId);
            Patient patient = patientService.getPatientByIdForEmail(patientId);
            System.out.println("Retrieved patientEmail: " + patient.getEmail());


            if (patient != null) {
                // Prepare message to publish
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("appointmentId", appointmentId);
                messageMap.put("patientId", patientId);
                messageMap.put("patientEmail", patient.getEmail());
                messageMap.put("patientFirstName", patient.getFirstName());
                messageMap.put("startTime", startTime);

                String messageToPublish = objectMapper.writeValueAsString(messageMap);

                // Publish to the topic
                mqttGateway.publishMessage(messageToPublish, "/appointments/with-email");
                System.out.println("Published message to /appointments/with-email");
            } else {
                System.err.println("Patient email not found for patientId: " + patientId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


