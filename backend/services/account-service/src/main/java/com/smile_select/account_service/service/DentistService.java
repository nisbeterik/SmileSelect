package com.smile_select.account_service.service;

import com.smile_select.account_service.model.Dentist;
import com.smile_select.account_service.repository.DentistRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Service
public class DentistService {

    @Autowired
    private DentistRepository dentistRepository;

    // Password Encrypter
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Find Dentist by Email
    public Optional<Dentist> findDentistByEmail(String email){
        return dentistRepository.findByEmail(email);
    }

    // Save Dentist to Database
    public Dentist saveDentist(Dentist dentist){
        dentist.setPassword(passwordEncoder.encode(dentist.getPassword()));
        return dentistRepository.save(dentist);
    }

    // Check encrypted password for Dentist
    public boolean checkPassword(Dentist dentist, String rawPassword) {
        return passwordEncoder.matches(rawPassword, dentist.getPassword());
    }
}
