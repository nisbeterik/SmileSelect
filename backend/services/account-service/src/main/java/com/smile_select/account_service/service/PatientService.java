package com.smile_select.account_service.service;

import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepository.save(patient);
    }

    public boolean checkPassword(Patient patient, String rawPassword) {
        return passwordEncoder.matches(rawPassword, patient.getPassword());
    }
}
