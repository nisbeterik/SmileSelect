package test.java.com.smile_select.patient_service.service;

import com.smile_select.patient_service.mqtt.MqttGateway;
import com.smile_select.patient_service.model.Patient;
import com.smile_select.patient_service.repository.PatientRepository;
import com.smile_select.patient_service.service.PatientService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    }
}
