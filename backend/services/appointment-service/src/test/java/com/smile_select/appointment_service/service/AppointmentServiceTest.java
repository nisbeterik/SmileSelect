package com.smile_select.appointment_service.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile_select.appointment_service.model.Appointment;
import com.smile_select.appointment_service.mqtt.MqttGateway;
import com.smile_select.appointment_service.repository.AppointmentRepository;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private MqttGateway mqttGateway;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();

        appointment.setDentistId(1L);
        appointment.setId(1L);
        appointment.setPatientId(null);
        appointment.setStartTime(LocalDateTime.of(2024, 12, 1, 9, 0));
        appointment.setEndTime(LocalDateTime.of(2024, 12, 1, 10, 0));

    }

    @Test
    void testPublishAppointmentCreatedEvent() throws Exception {
        
        appointment.setPatientId(100L);
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("patientId", appointment.getPatientId());
        messageMap.put("appointmentId", appointment.getId());
        messageMap.put("startTime", appointment.getStartTime());

        String expectedJson = "{\"appointmentId\":1,\"patientId\":100,\"startTime\":\"2024-12-01T09:00:00\"}";
        String actualJson = "{\"patientId\":100,\"appointmentId\":1,\"startTime\":\"2024-12-01T09:00:00\"}";
    
        appointmentService.publishAppointmentCreatedEvent(appointment);

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        verify(mqttGateway).publishMessage(actualJson, "/appointments/booked");
    }

}
