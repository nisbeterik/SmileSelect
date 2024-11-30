package com.smile_select.patient_service.service;

import com.smile_select.patient_service.dto.PatientUpdateDTO;
import com.smile_select.patient_service.exception.ResourceNotFoundException;
import com.smile_select.patient_service.model.Patient;
import com.smile_select.patient_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.criteria.Predicate;

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
    /*
     * public Patient getPatientById(Long id, String userEmail) {
     * // Mock authentication
     * String authenticatedEmail = "love.strandang@gmail.com"; // Use a hardcoded
     * email to simulate an authenticated
     * // user
     * return patientRepository.findById(id)
     * .filter(patient -> patient.getEmail().equals(authenticatedEmail))
     * .orElseThrow(() -> new
     * ResourceNotFoundException("Patient not found or access denied for ID: " +
     * id));
     * }
     */

    public Patient getPatientById(Long id, String userEmail) {
        return patientRepository.findById(id)
                .filter(patient -> patient.getEmail().equals(userEmail)) // Ensure only the owner can access their data
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

    //find a patient from partial information
    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }  

        final String finalSearchQuery = query.trim().toLowerCase();

        return patientRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            System.out.println(predicates + "innan");
            try {
                Long id = Long.parseLong(finalSearchQuery);
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            } catch (NumberFormatException error) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like( //checks email
                        criteriaBuilder.lower(root.get("email")),
                        "%" + finalSearchQuery + "%"
                    ),
                    criteriaBuilder.like( //checks first name
                        criteriaBuilder.lower(root.get("first_name")),
                        "%" + finalSearchQuery + "%"
                    ),
                    criteriaBuilder.like( //checks last name
                        criteriaBuilder.lower(root.get("last_name")),
                        "%" + finalSearchQuery + "%"
                    )
                ));
            }      
            System.out.println(predicates + "Efter");
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        });
    }
}
