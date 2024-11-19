package com.smile_select.account_service.service;

import com.smile_select.account_service.model.Dentist;
import com.smile_select.account_service.repository.DentistRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
/**
 * Service class to manage Dentist entities.
 * Provides methods to perform operations such as finding, saving, updating, and deleting dentist records.
 */
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

    // Find all dentists
    public List<Dentist> getAllDentists() {
        return dentistRepository.findAll();
    }

    // Find Dentist by id
    public Optional<Dentist> getDentistById(Long id) {
        return dentistRepository.findById(id);
    }

    // Update Dentist by id
    public Optional<Dentist> updateDentist(Long id, Dentist dentist) {
        return dentistRepository.findById(id).map(existingDentist -> {
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
            return dentistRepository.save(existingDentist);
        });
    }

    // Delete Dentist by id
    public void deleteDentist(Long id) {
        dentistRepository.deleteById(id);
    }
}
