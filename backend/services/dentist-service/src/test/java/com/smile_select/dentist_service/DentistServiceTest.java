package com.smile_select.dentist_service;

import com.smile_select.dentist_service.model.Dentist;
import com.smile_select.dentist_service.repository.DentistRepository;
import com.smile_select.dentist_service.service.DentistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DentistServiceTest {

    @Mock
    private DentistRepository dentistRepository;

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

    @Test
    void testFindDentistByEmail() {
        
        when(dentistRepository.findByEmail("dentist@example.com")).thenReturn(Optional.of(dentist));
        
        Optional<Dentist> result = dentistService.findDentistByEmail("dentist@example.com");
        
        assertEquals(true, result.isPresent());
        assertEquals("dentist@example.com", result.get().getEmail());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    void testFindDentistByEmailNotFound() {
       
        when(dentistRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        Optional<Dentist> result = dentistService.findDentistByEmail("nonexistent@example.com");
        assertEquals(false, result.isPresent());
    }

}