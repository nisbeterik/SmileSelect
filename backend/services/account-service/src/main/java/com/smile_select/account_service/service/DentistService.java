package main.java.com.smile_select.account_service.service;

import main.java.com.smile_select.account_service.repository.DentistRepository;

public class DentistService {

    @Autowired
    private DentistRepository dentistRepository;

    public Optional<Dentist> findDentistByEmail(String email){
        return dentistRepository.findDentistByEmail(email);
    }

    public Dentist saveDentist(Dentist dentist){
        return dentistRepository.save(dentist);
    }
}
