package com.smile_select.account_service.controller;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.smile_select.account_service.model.Dentist;
import com.smile_select.account_service.repository.DentistRepository;

/**
 * RESTful controller responsible for handling dentist registration and basic CRUD operations.
 * Exposes endpoints at the base path /api/accounts.
 */
@RestController
@RequestMapping("/api/accounts")
public class DentistRegistrationController {

    @Autowired
    private DentistRepository dentistRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Register a new dentist
     * @param dentist Dentist details
     * @return ResponseEntity with the response message
     */
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


    /**
     * Get ALL dentists
     * @return ResponseEntity with the list of all dentists
     */
    @GetMapping("/dentists")
    public ResponseEntity<List<Dentist>> getAllDentists() {
        List<Dentist> dentists = dentistRepository.findAll();
        return new ResponseEntity<>(dentists, HttpStatus.OK);
    }

    /**
     * GET /dentists/{id} - Get a dentist by their ID.
     * @param id The ID of the dentist.
     * @return The details of the dentist, or a 404 Not Found status if not found.
     */
    @GetMapping("/dentists/{id}")
    public ResponseEntity<Dentist> getDentistById(@PathVariable Long id) {
        Optional<Dentist> dentist = dentistRepository.findById(id);
        if (dentist.isPresent()) {
            return ResponseEntity.ok(dentist.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    /**
     * Updates an existing dentist's information
     * @param id the ID of the dentist to be updated
     * @param dentist the Dentist object with updated information
     * @return ResponseEntity with a message indicating the outcome of the update operation
     */
    @PutMapping("/dentists/{id}")
    public ResponseEntity<String> updateDentist (
            @PathVariable Long id,
            @Valid @RequestBody Dentist dentist) {
        Optional<Dentist> existingDentistOpt = dentistRepository.findById(id);

        if (existingDentistOpt.isPresent()) {
            Dentist existingDentist = existingDentistOpt.get();
            existingDentist.setFirstName(dentist.getFirstName());
            existingDentist.setLastName(dentist.getLastName());
            existingDentist.setEmail(dentist.getEmail());
            if (!dentist.getPassword().isEmpty()) {
                existingDentist.setPassword(passwordEncoder.encode(dentist.getPassword()));
            }
            existingDentist.setLongitude(dentist.getLongitude());
            existingDentist.setLatitude(dentist.getLatitude());
            existingDentist.setStreet(dentist.getStreet());
            existingDentist.setZip(dentist.getZip());
            existingDentist.setCity(dentist.getCity());
            existingDentist.setHouseNumber(dentist.getHouseNumber());
            dentistRepository.save(existingDentist);

            return ResponseEntity.ok("Dentist updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dentist not found");
        }
    }
    // TODO - DELETE /dentists/{id}

}
