package com.smile_select.account_service.service;

import com.smile_select.account_service.dto.DentistUpdateDTO;
import com.smile_select.account_service.exception.ResourceNotFoundException;
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

    public Optional<Dentist> findDentistByEmail(String email) {
        return dentistRepository.findByEmail(email);
    }

    public Dentist saveDentist(Dentist dentist) {
        // Hash the password before saving a dentist
        dentist.setPassword(passwordEncoder.encode(dentist.getPassword()));
        return dentistRepository.save(dentist);
    }

    public boolean checkPassword(Dentist dentist, String rawPassword) {
        return passwordEncoder.matches(rawPassword, dentist.getPassword());
    }

    public List<Dentist> getAllDentists() {
        return dentistRepository.findAll();
    }

    public Dentist getDentistById(Long id, String userEmail) {
        return dentistRepository.findById(id)
                .filter(dentist -> dentist.getEmail().equals(userEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found or access denied for ID: " + id));
    }

    public Dentist updateDentist(Long id, DentistUpdateDTO dentistUpdateDTO, String userEmail) {
        Dentist dentist = getDentistById(id, userEmail);
        if (dentistUpdateDTO.getFirstName() != null) {
            dentist.setFirstName(dentistUpdateDTO.getFirstName());
        }
        if (dentistUpdateDTO.getLastName() != null) {
            dentist.setLastName(dentistUpdateDTO.getLastName());
        }
        if (dentistUpdateDTO.getEmail() != null) {
            dentist.setEmail(dentistUpdateDTO.getEmail());
        }
        if (dentistUpdateDTO.getPassword() != null && !dentistUpdateDTO.getPassword().isEmpty()) {
            dentist.setPassword(passwordEncoder.encode(dentistUpdateDTO.getPassword()));
        }
        if (dentistUpdateDTO.getLongitude() != 0) {
            dentist.setLongitude(dentistUpdateDTO.getLongitude());
        }
        if (dentistUpdateDTO.getLatitude() != 0) {
            dentist.setLatitude(dentistUpdateDTO.getLatitude());
        }
        if (dentistUpdateDTO.getStreet() != null) {
            dentist.setStreet(dentistUpdateDTO.getStreet());
        }
        if (dentistUpdateDTO.getZip() != 0) {
            dentist.setZip(dentistUpdateDTO.getZip());
        }
        if (dentistUpdateDTO.getCity() != null) {
            dentist.setCity(dentistUpdateDTO.getCity());
        }
        if (dentistUpdateDTO.getHouseNumber() != null) {
            dentist.setHouseNumber(dentistUpdateDTO.getHouseNumber());
        }
        return dentistRepository.save(dentist);
    }

    public void deleteDentistById(Long id, String userEmail) {
        Dentist dentist = getDentistById(id, userEmail);
        dentistRepository.delete(dentist);
    }
}
