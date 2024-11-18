package com.smile_select.account_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.repository.PatientRepository;

// Marks the class as a RESTful controller
// Sets the base path for all endpoints in this controller to /api/accounts
@RestController
@RequestMapping("/api/accounts")
public class PatientRegistrationController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Inject Patient Repository
    public PatientRegistrationController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostMapping("/patients")
    public ResponseEntity<String> registerPatient(@RequestBody Patient patient) {
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));

        // Save the patient
        patientRepository.save(patient);

        // Simulate saving the patient data
        System.out.println("Received patient registration data: " + patient);

        // Respond with success message
        return new ResponseEntity<>("Patient registered successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        // Return all patients in JSON format
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }
}
