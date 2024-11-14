package main.java.com.smile_select.account_service.service;

import main.java.com.smile_select.account_service.model.Dentist;
import main.java.com.smile_select.account_service.repository.DentistRepository;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DentistService {

    @Autowired
    private DentistRepository dentistRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<Dentist> findDentistByEmail(String email){
        return dentistRepository.findDentistByEmail(email);
    }

    public Dentist saveDentist(Dentist dentist){
        dentist.setPassword(passwordEncoder.encode(dentist.getPassword()));
        return dentistRepository.save(dentist);
    }
    public boolean checkPassword(Dentist dentist, String rawPassword) {
        return passwordEncoder.matches(rawPassword, dentist.getPassword());
    }
}
