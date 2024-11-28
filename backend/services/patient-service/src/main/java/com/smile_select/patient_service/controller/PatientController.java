package com.smile_select.patient_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.smile_select.patient_service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.smile_select.patient_service.dto.PatientDTO;
import com.smile_select.patient_service.model.Patient;
import com.smile_select.patient_service.service.PatientService;
import com.smile_select.patient_service.dto.PatientUpdateDTO;

// Marks the class as a RESTful controller
// Sets the base path for all endpoints in this controller to /api/accounts
@RestController
@RequestMapping("/api/patients")
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

    // Get patient by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Patient> getPatientByEmail(@PathVariable("email") String email) {
        Patient patient = patientService.findPatientByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
        return ResponseEntity.ok(patient);
    }

    // Get patient by ID
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Long id) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientService.getPatientById(id, userEmail);

        PatientDTO response = new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(),
                patient.getEmail());
        return ResponseEntity.ok(response);
    }
    //find patient
    @GetMapping("/search")
        public ResponseEntity<List<PatientDTO>> searchPatients(
            @RequestParam(required = false) String query
        ) {
            List<PatientDTO> patients = patientService.searchPatients(query).stream()
                .map(patient -> new PatientDTO(
                    patient.getId(), 
                    patient.getFirstName(), 
                    patient.getLastName(), 
                    patient.getEmail()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(patients);
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

    // Update patient details
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePatientDetails(
            @PathVariable("id") Long id,
            @Valid @RequestBody PatientUpdateDTO updateDetails) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        patientService.updatePatientDetails(id, userEmail, updateDetails);
        return ResponseEntity.ok("Patient details updated successfully");
    }

    // Delete a patient
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatientById(@PathVariable("id") Long id) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        patientService.deletePatientById(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
