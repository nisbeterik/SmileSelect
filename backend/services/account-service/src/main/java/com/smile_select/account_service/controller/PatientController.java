package com.smile_select.account_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.smile_select.account_service.exception.ResourceNotFoundException;
import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.repository.PatientRepository;

import jakarta.servlet.http.HttpServletRequest;

// Marks the class as a RESTful controller
// Sets the base path for all endpoints in this controller to /api/accounts
@RestController
@RequestMapping("/api/accounts/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<String> registerPatient(@RequestBody Patient patient) {
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));

        // Save the patient
        patientRepository.save(patient);

        // Simulate saving the patient data
        System.out.println("Received patient registration data: " + patient);

        // Respond with success message
        return new ResponseEntity<>("Patient registered successfully!", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        // Return all patients in JSON format
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable("id") Long id, HttpServletRequest request) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isEmpty() || !patient.get().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Provided patient not found or Access denied");
        }

        return new ResponseEntity<>(patient.get(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatientDetails(@PathVariable("id") Long id) {
        return null;

    }

    // add endpoint for updating password
}
