package com.smile_select.appointment_service.service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smile_select.appointment_service.model.Appointment;
import com.smile_select.appointment_service.repository.AppointmentRepository;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

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
}
