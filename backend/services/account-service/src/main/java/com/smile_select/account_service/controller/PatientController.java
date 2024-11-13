package com.smile_select.account_service.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Marks the class as a RESTful controller
// Sets the base path for all endpoints in this controller to /api
@RestController
@RequestMapping("/api")
public class PatientController {

    public ResponseEntity<String> registerPatient (@RequestBody Patient patient) {
        // Simulate saving the patient data
        System.out.println("Received paetinet registration data: " + patient);

        // Respond with success message
        return new ResponseEntity<>("Patient registered successfully!", HttpStatus.OK);
    }
}
