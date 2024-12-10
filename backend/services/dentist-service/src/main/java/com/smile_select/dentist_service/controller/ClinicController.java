package com.smile_select.dentist_service.controller;

import com.smile_select.dentist_service.dto.ClinicDTO;
import com.smile_select.dentist_service.dto.ClinicUpdateDTO;
import com.smile_select.dentist_service.model.Clinic;
import com.smile_select.dentist_service.service.ClinicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/dentists/clinics")
public class ClinicController {

    @Autowired
    private ClinicService clinicService;

    /**
     * GET /clinics - Fetch all clinics
     * Response: 200 OK, return JSON list of clinics
     */
    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        List<Clinic> clinics = clinicService.getAllClinics();
        return ResponseEntity.ok(clinics);
    }

    /**
     * GET /clinics/{id} - Fetch a specific clinic by ID
     * Response: 200 OK if found, else 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable("id") long id) {
        return clinicService.getClinicById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /clinics - Create a new clinic
     * Validates input data. If valid, returns 201 Created with the created clinic data.
     * If invalid, returns 400 Bad Request.
     */
    @PostMapping
    public ResponseEntity<Clinic> createClinic(@Valid @RequestBody ClinicDTO clinicDTO) {
        Clinic createdClinic = clinicService.createClinic(clinicDTO);
        URI location = URI.create("/api/dentists/clinics/" + createdClinic.getId());
        return ResponseEntity.created(location).body(createdClinic);
    }

    /**
     * PUT /clinics/{id} - Update an existing clinic
     * If the clinic exists and data is valid, returns 200 OK with updated clinic data.
     * If clinic not found, returns 404 Not Found.
     * If data invalid, returns 400 Bad Request.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Clinic> updateClinic(
            @PathVariable("id") long id,
            @Valid @RequestBody ClinicUpdateDTO clinicUpdateDTO) {

        return clinicService.updateClinic(id, clinicUpdateDTO)
                .map(updatedClinic -> ResponseEntity.ok(updatedClinic))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE /clinics/{id} - Delete a clinic by ID
     * If clinic found and deleted, return 204 No Content.
     * If not found, return 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable("id") long id) {
        boolean deleted = clinicService.deleteClinic(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /clinics/{id} - Partially update an existing clinic
     * If clinic exists and data is valid, return 200 OK with updated clinic data.
     * If clinic not found, return 404 Not Found.
     * If data is invalid, return 400 Bad Request.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Clinic> patchClinic(
            @PathVariable("id") long id,
            @Valid @RequestBody ClinicUpdateDTO clinicPatchDTO) {
        return clinicService.patchClinic(id, clinicPatchDTO)
                .map(updatedClinic -> ResponseEntity.ok(updatedClinic))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
