package com.smile_select.patient_service.service;

import com.smile_select.account_service.dto.PatientUpdateDTO;
import com.smile_select.account_service.exception.ResourceNotFoundException;
import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    // Update patient details
    public void updatePatientDetails(Long id, String userEmail, PatientUpdateDTO updateDetails) {
        Patient patient = getPatientById(id, userEmail);

        if (updateDetails.getFirstName() != null) {
            patient.setFirstName(updateDetails.getFirstName());
        }
        if (updateDetails.getLastName() != null) {
            patient.setLastName(updateDetails.getLastName());
        }
        if (updateDetails.getEmail() != null) {
            patient.setEmail(updateDetails.getEmail());
        }
        if (updateDetails.getDateOfBirth() != null) {
            patient.setDateOfBirth(updateDetails.getDateOfBirth());
        }
        patientRepository.save(patient);
    }

}
