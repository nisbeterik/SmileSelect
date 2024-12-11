package com.smile_select.patient_service.service;

import com.smile_select.patient_service.mqtt.MqttGateway;
import com.smile_select.patient_service.model.Patient;
import com.smile_select.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Mock
    private MqttGateway mqttGateway;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setEmail("patient@example.com");
        patient.setFirstName("James");
        patient.setLastName("Bond");
        patient.setPassword("password");
        patient.setRole("PATIENT");

        patient.setDateOfBirth(LocalDate.of(2000, 1, 1));
    }

    /***
     * Tests for getPatientId()
     */
    @Test
    public void testGetPatientById_PatientNotFound() {

        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Patient> result = patientService.getPatientById(1L);

        assertFalse(result.isPresent());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPatientById_InvalidPatientId() {

        when(patientRepository.findById(null)).thenThrow(new IllegalArgumentException("Patient ID must not be null"));

        Optional<Patient> result = patientService.getPatientById(null);

        assertFalse(result.isPresent());
        verify(patientRepository, times(1)).findById(null);
    }

    @Test
    public void testGetPatientById_ValidId() {

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.getPatientById(1L);

        assertTrue(result.isPresent(), "Expected result to contain a patient");
        assertEquals(1L, result.get().getId(), "Patient ID should match");
        assertEquals("James", result.get().getFirstName(), "First name should match");
        assertEquals("Bond", result.get().getLastName(), "Last name should match");

        verify(patientRepository, times(1)).findById(1L);
    }

}
