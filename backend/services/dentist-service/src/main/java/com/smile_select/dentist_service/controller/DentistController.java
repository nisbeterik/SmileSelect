package com.smile_select.dentist_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.smile_select.dentist_service.dto.DentistDTO;
import com.smile_select.dentist_service.exception.ResourceNotFoundException;
import com.smile_select.dentist_service.model.Clinic;
import com.smile_select.dentist_service.repository.ClinicRepository;
import com.smile_select.dentist_service.service.DentistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.smile_select.dentist_service.model.Dentist;

/**
 * RESTful controller responsible for handling dentist registration and basic
 * CRUD operations.
 * Exposes endpoints at the base path /api/accounts.
 */
@RestController
@RequestMapping("/api/dentists")
public class DentistController {

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
    @PostMapping
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

        dentistService.saveDentist(dentist);

        return new ResponseEntity<>("Dentist registered successfully!", HttpStatus.CREATED);
    }

    /**
     * Get ALL dentists
     *
     * @return ResponseEntity with the list of all dentists
     */
    @GetMapping
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
     *         Ensures only the authenticated user can access
     */
    @GetMapping("/{id}")
    public ResponseEntity<DentistDTO> getDentistById(@PathVariable("id") Long id) {

        Dentist dentist = dentistService.findById(id);

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
                dentist.getClinic().getHouseNumber());

        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing dentist's information
     *
     * @param id      - the ID of the dentist to be updated
     * @param dentist the Dentist object with updated information
     * @return ResponseEntity with a message indicating the outcome of the update
     *         operation
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateDentist(
            @PathVariable Long id,
            @RequestBody Dentist dentistUpdate) {

        // Fetch the existing dentist
        Dentist existingDentist = dentistService.findById(id);

        // ADD AUTHORIZATION LOGIC HERE, COMPARE ID IN PARAM TO ID IN TOKEN

        // Update only the provided fields
        if (dentistUpdate.getFirstName() != null) {
            existingDentist.setFirstName(dentistUpdate.getFirstName());
        }
        if (dentistUpdate.getLastName() != null) {
            existingDentist.setLastName(dentistUpdate.getLastName());
        }
        if (dentistUpdate.getEmail() != null) {
            existingDentist.setEmail(dentistUpdate.getEmail());
        }
        if (dentistUpdate.getPassword() != null && !dentistUpdate.getPassword().isEmpty()) {
            // Ensure the password is encoded before saving
            existingDentist.setPassword(passwordEncoder.encode(dentistUpdate.getPassword()));
        }

        // Update the clinic if a new clinicId is provided
        if (dentistUpdate.getClinicId() != null) {
            Optional<Clinic> selectedClinic = clinicRepository.findById(dentistUpdate.getClinicId());
            if (selectedClinic.isEmpty()) {
                return ResponseEntity.badRequest().body("Selected clinic does not exist");
            }
            existingDentist.setClinic(selectedClinic.get());
        }

        // Save the updated dentist, including the encoded password
        dentistService.saveDentist(existingDentist);

        return ResponseEntity.ok("Dentist updated successfully");
    }

    /**
     * Deletes the dentist with the specified ID.
     *
     * @param id - the ID of the dentist to delete
     * @return a ResponseEntity containing a success message if the dentist was
     *         deleted,
     *         or a not found message if no dentist with the specified ID exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDentist(@PathVariable Long id) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dentistService.deleteDentistById(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-email")
    public ResponseEntity<Dentist> getDentistByEmail(@RequestParam("email") String email) {
        Dentist dentist = dentistService.findDentistByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with email: " + email));
        return ResponseEntity.ok(dentist);
    }

}
