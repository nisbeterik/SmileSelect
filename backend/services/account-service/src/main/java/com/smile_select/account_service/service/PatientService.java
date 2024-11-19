package com.smile_select.account_service.service;

import com.smile_select.account_service.exception.ResourceNotFoundException;
import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class PatientService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private PatientRepository patientRepository;

    public Optional<Patient> findPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public Patient savePatient(Patient patient) {
        // Hash the password before saving a patient
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepository.save(patient);
    }

    public boolean checkPassword(Patient patient, String rawPassword) {
        return passwordEncoder.matches(rawPassword, patient.getPassword());
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Retrieve patient by ID
    public Patient getPatientById(Long id, String userEmail) {
        return patientRepository.findById(id)
                .filter(patient -> patient.getEmail().equals(userEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found or access denied for ID: " + id));
    }

    // Delete a patient
    public void deletePatientById(Long id, String userEmail) {
        Patient patient = getPatientById(id, userEmail);
        patientRepository.delete(patient);
    }

}
