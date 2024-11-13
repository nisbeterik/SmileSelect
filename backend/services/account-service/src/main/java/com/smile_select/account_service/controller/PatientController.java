package com.smile_select.account_service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smile_select.account_service.model.Patient;

// Marks the class as a RESTful controller
// Sets the base path for all endpoints in this controller to /api
@RestController
@RequestMapping("/api/accounts")
public class PatientController {

    // For practical testing, change for DB later on
    private static final List<Patient> patients = new ArrayList<>();

    @PostMapping("/patients")
    public ResponseEntity<String> registerPatient(@RequestBody Patient patient) {
        patients.add(patient);
        // Simulate saving the patient data
        System.out.println("Received paetinet registration data: " + patient);

        // Respond with success message
        return new ResponseEntity<>("Patient registered successfully!", HttpStatus.OK);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        // Return all patients in JSON format
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }
}
