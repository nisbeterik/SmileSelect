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
import com.smile_select.patient_service.model.PatientPreferredDate;
import com.smile_select.patient_service.service.PatientService;
import com.smile_select.patient_service.dto.PatientUpdateDTO;
import com.smile_select.patient_service.dto.PatientPreferredDateDTO;

// Marks the class as a RESTful controller
// Sets the base path for all endpoints in this controller to /api/accounts
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Register new patient
    @PostMapping("/register")
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
        Patient patient = patientService.getPatientByIdAndEmail(id, userEmail);

        PatientDTO response = new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(),
                patient.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking/{id:[0-9]+}")
    public ResponseEntity<PatientDTO> getPatientByIdAsDentist(@PathVariable("id") Long id) {
        // String userEmail = (String)
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientService.getPatientByIdAsDentist(id);

        PatientDTO response = new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(),
                patient.getEmail());
        return ResponseEntity.ok(response);
    }

    // find patient
    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(
            @RequestParam(required = false) String query) {
        List<PatientDTO> patients = patientService.searchPatients(query).stream()
                .map(patient -> new PatientDTO(
                        patient.getId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getEmail()))
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

    @PostMapping("/preferred-date")
    public ResponseEntity<?> createPatientPreferredDate(@RequestBody PatientPreferredDate request) {
        if (request.getPatient() == null || request.getPatient().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient ID is required.");
        }

        Patient patient = patientService.getPatientById(request.getPatient().getId());
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }

        request.setPatient(patient);

        // Check if there are already 5 preferred dates
        if (patient.getPreferredDates().size() >= 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Patient cannot have more than 5 preferred dates.");
        }

        // Check if the preferred date already exists in the patient's preferred dates
        if (patient.getPreferredDates().contains(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Preferred date already exists for this patient.");
        }

        // Save the new preferred date
        PatientPreferredDate savedPreferredDate = patientService.savePatientPreferredDate(request);

        PatientPreferredDateDTO responseDTO = new PatientPreferredDateDTO(
                savedPreferredDate.getId(),
                savedPreferredDate.getPatient().getId(),
                savedPreferredDate.getPreferredDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}
