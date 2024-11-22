package com.smile_select.dentist_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.smile_select.account_service.dto.DentistDTO;
import com.smile_select.account_service.model.Clinic;
import com.smile_select.account_service.repository.ClinicRepository;
import com.smile_select.account_service.service.DentistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class DentistController {

    @Autowired
    private DentistRepository dentistRepository;
    @Autowired
    private ClinicRepository clinicRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DentistService dentistService;


    /**
     * Register a new dentist
     *
     * @param dentist Dentist details
     * @return ResponseEntity with the response message
     */
    @PostMapping("/dentists")
    public ResponseEntity<String> registerDentist(@Valid @RequestBody Dentist dentist) {
        if (dentistService.findDentistByEmail(dentist.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email is already in use", HttpStatus.BAD_REQUEST);
        }

        // Validate and fetch the clinic
        if (dentist.getClinicId() == null) {
            return new ResponseEntity<>("Clinic ID is required", HttpStatus.BAD_REQUEST);
        }
        Optional<Clinic> selectedClinic = clinicRepository.findById(dentist.getClinicId());
        if (selectedClinic.isEmpty()) {
            return new ResponseEntity<>("Selected clinic does not exist", HttpStatus.BAD_REQUEST);
        }

        // Set the clinic and save the dentist
        dentist.setClinic(selectedClinic.get());
        dentist.setPassword(passwordEncoder.encode(dentist.getPassword()));

        dentistRepository.save(dentist);

        return new ResponseEntity<>("Dentist registered successfully!", HttpStatus.CREATED);
    }


    /**
     * Get ALL dentists
     *
     * @return ResponseEntity with the list of all dentists
     */
    @GetMapping("/dentists")
    public ResponseEntity<List<DentistDTO>> getAllDentists() {
        List<DentistDTO> dentists = dentistService.getAllDentists().stream()
                .map(dentist -> new DentistDTO(
                        dentist.getId(),
                        dentist.getFirstName(),
                        dentist.getLastName(),
                        dentist.getEmail(),
                        dentist.getClinic().getId(),
                        dentist.getClinic().getName(),
                        dentist.getClinic().getLongitude(),
                        dentist.getClinic().getLatitude(),
                        dentist.getClinic().getStreet(),
                        dentist.getClinic().getZip(),
                        dentist.getClinic().getCity(),
                        dentist.getClinic().getHouseNumber()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dentists);
    }

    /**
     * GET /dentists/{id} - Get a dentist by their ID.
     *
     * @param id - The ID of the dentist.
     * @return The details of the dentist, or a 404 Not Found status if not found.
     * Ensures only the authenticated user can access
     */
    @GetMapping("/dentists/{id}")
    public ResponseEntity<DentistDTO> getDentistById(@PathVariable("id") Long id) {
        // Retrieve the authenticated dentist's email
        String dentistEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Dentist dentist = dentistService.getDentistById(id, dentistEmail);

        // Map dentist details along with associated clinic details to the DTO
        DentistDTO response = new DentistDTO(
                dentist.getId(),
                dentist.getFirstName(),
                dentist.getLastName(),
                dentist.getEmail(),
                dentist.getClinic().getId(),
                dentist.getClinic().getName(),
                dentist.getClinic().getLongitude(),
                dentist.getClinic().getLatitude(),
                dentist.getClinic().getStreet(),
                dentist.getClinic().getZip(),
                dentist.getClinic().getCity(),
                dentist.getClinic().getHouseNumber()
        );

        return ResponseEntity.ok(response);
    }


    /**
     * Updates an existing dentist's information
     *
     * @param id      - the ID of the dentist to be updated
     * @param dentist the Dentist object with updated information
     * @return ResponseEntity with a message indicating the outcome of the update operation
     */
    @PutMapping("/dentists/{id}")
    public ResponseEntity<String> updateDentist(
            @PathVariable Long id,
            @Valid @RequestBody Dentist dentist) {
        Optional<Dentist> existingDentistOpt = dentistRepository.findById(id);

        if (existingDentistOpt.isPresent()) {
            Dentist existingDentist = existingDentistOpt.get();

            // Update basic details
            existingDentist.setFirstName(dentist.getFirstName());
            existingDentist.setLastName(dentist.getLastName());
            existingDentist.setEmail(dentist.getEmail());
            if (dentist.getPassword() != null && !dentist.getPassword().isEmpty()) {
                existingDentist.setPassword(passwordEncoder.encode(dentist.getPassword()));
            }

            // Update the associated clinic if a new clinicId is provided
            if (dentist.getClinicId() != null) {
                Optional<Clinic> selectedClinic = clinicRepository.findById(dentist.getClinicId());
                if (selectedClinic.isEmpty()) {
                    return new ResponseEntity<>("Selected clinic does not exist", HttpStatus.BAD_REQUEST);
                }
                existingDentist.setClinic(selectedClinic.get());
            }

            // Save updated dentist
            dentistRepository.save(existingDentist);

            return ResponseEntity.ok("Dentist updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dentist not found");
        }
    }


    /**
     * Deletes the dentist with the specified ID.
     *
     * @param id - the ID of the dentist to delete
     * @return a ResponseEntity containing a success message if the dentist was deleted,
     * or a not found message if no dentist with the specified ID exists
     */
    @DeleteMapping("/dentists/{id}")
    public ResponseEntity<String> deleteDentist(@PathVariable Long id) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dentistService.deleteDentistById(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
