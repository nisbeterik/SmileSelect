package com.smile_select.dentist_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smile_select.dentist_service.model.Dentist;
import com.smile_select.dentist_service.mqtt.MqttGateway;
import com.smile_select.dentist_service.repository.DentistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DentistServiceTest {

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private MqttGateway mqttGateway;

    @InjectMocks
    private DentistService dentistService;

    private Dentist dentist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dentist = new Dentist();
        dentist.setEmail("dentist@example.com");
        dentist.setFirstName("John");
        dentist.setLastName("Doe");
        dentist.setPassword("password");
    }

    /***
     * findDentistByEmail() tests
     ***/

    // Test for a valid input email that exists
    @Test
    void testFindDentistByEmail() {

        when(dentistRepository.findByEmail("dentist@example.com")).thenReturn(Optional.of(dentist));

        Optional<Dentist> result = dentistService.findDentistByEmail("dentist@example.com");

        assertEquals(true, result.isPresent());
        assertEquals("dentist@example.com", result.get().getEmail());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    // Test for if email input does not exist
    @Test
    void testFindDentistByEmailNotFound() {

        when(dentistRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        Optional<Dentist> result = dentistService.findDentistByEmail("nonexistent@example.com");
        assertEquals(false, result.isPresent());
    }

    // Test for email input being valid
    @Test
    void testFindDentistByEmailValidEmailInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dentistService.findDentistByEmail("invalid-email");
        });

        assertEquals("Invalid email format: invalid-email", exception.getMessage());
    }

    // Test for email input being null
    @Test
    void testFindDentistByEmailNullEmailInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dentistService.findDentistByEmail(null);
        });
        assertEquals("Invalid email format: null", exception.getMessage());
    }

    // Test for email input being an empty string
    @Test
    void testFindDentistByEmailEmptyEmailInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dentistService.findDentistByEmail("");
        });

        assertEquals("Invalid email format: ", exception.getMessage());
    }

    /***
     * Tests for appointment cancellation
     */


    // Tests handleAppointmentCancellation for valid payloads.
    @Test
    void testHandleAppointmentCancellationValidPayload() throws Exception {

        String payload = "{ \"startTime\": \"2024-12-08T10:00:00\", \"dentistId\": 1, \"id\": 123 }";
        Dentist dentist = new Dentist();
        dentist.setFirstName("John");
        dentist.setEmail("john.doe@example.com");

        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));

        dentistService.handleAppointmentCancellation(payload);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);

        verify(mqttGateway).publishMessage(messageCaptor.capture(), topicCaptor.capture());

        assertEquals("/notifications/cancelled-by-patient", topicCaptor.getValue());

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Map<String, Object> expectedMessage = new HashMap<>();
        expectedMessage.put("dentistFirstName", "John");
        expectedMessage.put("dentistEmail", "john.doe@example.com");
        expectedMessage.put("appointmentStartTime", "2024-12-08T10:00:00");
        expectedMessage.put("appointmentId", 123);

        String expectedMessageJson = objectMapper.writeValueAsString(expectedMessage);

        assertEquals(expectedMessageJson, messageCaptor.getValue());
    }
}