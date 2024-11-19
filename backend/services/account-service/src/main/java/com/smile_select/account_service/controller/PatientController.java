package com.smile_select.account_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.smile_select.account_service.dto.PatientDTO;
import com.smile_select.account_service.exception.ResourceNotFoundException;
import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.repository.PatientRepository;
import com.smile_select.account_service.service.PatientService;
import com.smile_select.account_service.dto.PatientUpdateDTO;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

// Marks the class as a RESTful controller
// Sets the base path for all endpoints in this controller to /api/accounts
@RestController
@RequestMapping("/api/accounts/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Register new patient
    @PostMapping
    public ResponseEntity<String> registerPatient(@Valid @RequestBody Patient patient) {
        if (patientService.findPatientByEmail(patient.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email is already in use", HttpStatus.BAD_REQUEST);
        }

        patientService.savePatient(patient);
        return new ResponseEntity<>("Patient registered successfully!", HttpStatus.CREATED);
    }

    // Get a specific patient by ID (ensures only the authenticated user can access)
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Long id) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientService.getPatientById(id, userEmail);

        // Map to DTO
        PatientDTO response = new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(),
                patient.getEmail());
        return ResponseEntity.ok(response);
    }

    // Get all patients
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients().stream()
                .map(patient -> new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(),
                        patient.getEmail()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatientDetails(@PathVariable("id") Long id) {
        return null;
        // TODO
    }

    // Delete a patient
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatientById(@PathVariable("id") Long id) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        patientService.deletePatientById(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // add endpoint for updating password
}
