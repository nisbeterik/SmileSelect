package com.smile_select.dentist_service.service;

import com.smile_select.dentist_service.dto.ClinicDTO;
import com.smile_select.dentist_service.dto.ClinicUpdateDTO;
import com.smile_select.dentist_service.model.Clinic;
import com.smile_select.dentist_service.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicService {

    @Autowired
    private ClinicRepository clinicRepository;

    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    public Optional<Clinic> getClinicById(long id) {
        return clinicRepository.findById(id);
    }

    public Clinic createClinic(ClinicDTO clinicDTO) {
        Clinic clinic = new Clinic();
        clinic.setName(clinicDTO.getName());
        clinic.setLongitude(clinicDTO.getLongitude());
        clinic.setLatitude(clinicDTO.getLatitude());
        clinic.setStreet(clinicDTO.getStreet());
        clinic.setZip(clinicDTO.getZip());
        clinic.setCity(clinicDTO.getCity());
        clinic.setHouseNumber(clinicDTO.getHouseNumber());
        return clinicRepository.save(clinic);
    }

    public Optional<Clinic> updateClinic(long id, ClinicUpdateDTO clinicUpdateDTO) {
        return clinicRepository.findById(id).map(clinic -> {
            clinic.setName(clinicUpdateDTO.getName());
            clinic.setLongitude(clinicUpdateDTO.getLongitude());
            clinic.setLatitude(clinicUpdateDTO.getLatitude());
            clinic.setStreet(clinicUpdateDTO.getStreet());
            clinic.setZip(clinicUpdateDTO.getZip());
            clinic.setCity(clinicUpdateDTO.getCity());
            clinic.setHouseNumber(clinicUpdateDTO.getHouseNumber());
            return clinicRepository.save(clinic);
        });
    }

    public boolean deleteClinic(long id) {
        return clinicRepository.findById(id).map(clinic -> {
            clinicRepository.delete(clinic);
            return true;
        }).orElse(false);
    }
}
