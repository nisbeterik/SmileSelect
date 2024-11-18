package com.smile_select.account_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.smile_select.account_service.model.Dentist;
import com.smile_select.account_service.repository.DentistRepository;

//Marks the class as a RESTful controller 
// Sets the base path for all endpoints in this controller to /api/accounts
@RestController
@RequestMapping("/api/accounts")
public class DentistRegistrationController {

    @Autowired
    private DentistRepository dentistRepository;

     @Autowired
    private PasswordEncoder passwordEncoder;

    // Inject Dentist Repository
    public DentistRegistrationController(DentistRepository dentistRepository) {
        this.dentistRepository = dentistRepository;
    }

    @PostMapping("/dentists")
    public ResponseEntity<String> registerDentist(@RequestBody Dentist dentist) {
        dentist.setPassword(passwordEncoder.encode(dentist.getPassword()));

        // Save the dentist
        dentistRepository.save(dentist);

        // Simulate saving the dentist data
        System.out.println("Received dentist registration data: " + dentist);

        // Respond with success message
        return new ResponseEntity<>("Dentist registered successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/dentists")
    public ResponseEntity<List<Dentist>> getAllDentists() {
        List<Dentist> dentists = dentistRepository.findAll();
        // Return all dentists in JSON format
        return new ResponseEntity<>(dentists, HttpStatus.OK);
    }
}
