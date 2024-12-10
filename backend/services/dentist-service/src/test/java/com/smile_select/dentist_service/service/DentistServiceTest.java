package com.smile_select.dentist_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smile_select.dentist_service.exception.ResourceNotFoundException;
import com.smile_select.dentist_service.model.Dentist;
import com.smile_select.dentist_service.model.Clinic;
import com.smile_select.dentist_service.repository.ClinicRepository;
import com.smile_select.dentist_service.repository.DentistRepository;
import com.smile_select.dentist_service.mqtt.MqttGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DentistServiceTest {

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private MqttGateway mqttGateway;

    @InjectMocks
    private DentistService dentistService;

    private Dentist dentist;
    private Clinic clinic;

    @BeforeEach
    void setUp() {
        dentist = new Dentist();
        dentist.setId(1L);
        dentist.setEmail("dentist@example.com");
        dentist.setFirstName("John");
        dentist.setLastName("Doe");
        dentist.setPassword("password");

        clinic = new Clinic();
        clinic.setId(1L);
        clinic.setName("Dental Clinic");
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
    

    /**
     * Tests for saveDentist()
     */
    @Test
    void testSaveDentist() {
        when(dentistRepository.save(any(Dentist.class))).thenReturn(dentist);

        Dentist savedDentist = dentistService.saveDentist(dentist);

        assertNotNull(savedDentist);
        assertEquals("dentist@example.com", savedDentist.getEmail());
    }

    /**
     * Test getAllDentists()
     */

    @Test
    void testGetAllDentists() {
        List<Dentist> dentistList = new ArrayList<>();
        dentistList.add(dentist);

        when(dentistRepository.findAll()).thenReturn(dentistList);

        List<Dentist> result = dentistService.getAllDentists();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    /**
     * Tests for findById()
     */

    @Test
    void testFindById() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));

        Dentist result = dentistService.findById(1L);

        assertNotNull(result);
        assertEquals("dentist@example.com", result.getEmail());
    }

    @Test
    void testFindByIdNotFound() {
        when(dentistRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            dentistService.findById(2L);
        });

        assertEquals("Dentist not found with ID: 2", exception.getMessage());
    }


    /**
     * Tests for updateDentist() 
     */
    @Test
    void testUpdateDentist() {
        // Arrange
        // Mock existing dentist to be updated
        Dentist existingDentist = new Dentist();
        existingDentist.setFirstName("John");
        existingDentist.setLastName("Doe");
        existingDentist.setEmail("john.doe@example.com");
        existingDentist.setPassword("oldpassword");
    
        // Mock clinic object for association
        Clinic clinic = new Clinic();
        clinic.setId(1L);
        clinic.setName("Dental Clinic");
    
        // Mock the responses from the repositories
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(existingDentist));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
    
        // Create an updated dentist object
        Dentist updatedDentist = new Dentist();
        updatedDentist.setFirstName("UpdatedFirstName");
        updatedDentist.setLastName("UpdatedLastName");
        updatedDentist.setEmail("updated@example.com");
        updatedDentist.setPassword("newpassword");
        updatedDentist.setClinicId(1L); // Setting the clinicId for association
    
        // Mock the behavior of the save method to return the updated dentist
        when(dentistRepository.save(any(Dentist.class))).thenReturn(existingDentist); // Returning the updated object
    
        // Act
        Dentist result = dentistService.updateDentist(1L, updatedDentist);
    
        // Assert
        assertNotNull(result); // Ensure that the result is not null
        assertEquals("UpdatedFirstName", result.getFirstName());
        assertEquals("UpdatedLastName", result.getLastName());
        assertEquals("updated@example.com", result.getEmail());
        assertNotNull(result.getClinic()); // Ensure the clinic is set
        assertEquals(clinic.getId(), result.getClinic().getId()); // Ensure the clinic is the one associated
    }

    @Test
    void testUpdateDentistNotFound() {
        Dentist updatedDentist = new Dentist();
        updatedDentist.setFirstName("UpdatedFirstName");

        when(dentistRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            dentistService.updateDentist(1L, updatedDentist);
        });

        assertEquals("Dentist not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteDentistById() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));

        dentistService.deleteDentistById(1L, "dentist@example.com");

        verify(dentistRepository).delete(dentist);
    }

    @Test
    void testDeleteDentistByIdNotFound() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            dentistService.deleteDentistById(1L, "dentist@example.com");
        });

        assertEquals("Dentist not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteDentistByIdAccessDenied() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            dentistService.deleteDentistById(1L, "other@example.com");
        });

        assertEquals("Access denied for deleting dentist ID: 1", exception.getMessage());
    }

}