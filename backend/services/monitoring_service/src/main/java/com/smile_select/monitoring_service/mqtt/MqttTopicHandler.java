package com.smile_select.monitoring_service.mqtt;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile_select.monitoring_service.service.MonitoringService;
import org.springframework.integration.mqtt.support.MqttHeaders;

@Component
public class MqttTopicHandler {

    private final MonitoringService monitoringService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MqttTopicHandler(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(
            Message<String> message,
            @Header(MqttHeaders.RECEIVED_TOPIC) String topic) {
        switch (topic) {
            case "/login-success":
                handleLoginEvent(message.getPayload());
                break;

            case "/appointments/booked-by-patient":
                handleBookingEvent(message.getPayload());
                break;

            case "/appointments/booked-by-dentist":
                handleBookingEvent(message.getPayload());
                break;

            case "/appointments/created":
                handleCreationEvent(message.getPayload());
                break;
        }
    }

    private void handleLoginEvent(String payload){
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            Long id = jsonNode.get("id").asLong();
            String role = jsonNode.get("role").asText();

            if (role.equals("PATIENT")){
                monitoringService.recordPatientLogin(id);
            }
            else if (role.equals("DENTIST")){
                monitoringService.recordDentistLogin(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBookingEvent(String payload){
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            Long id = jsonNode.get("id").asLong();
            monitoringService.recordAppointmentSlotBooked(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCreationEvent(String payload){
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            Long id = jsonNode.get("id").asLong();
            monitoringService.recordAppointmentSlotCreation(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
