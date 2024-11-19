package com.smile_select.account_service.controller;

import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.repository.PatientRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/accounts/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable("id") Long id){
        Optional<Patient> patient = patientRepository.findById(id);

        if (patient.isPresent()) {
            return new ResponseEntity<>(patient, HttpStatus.OK);
        }
        return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
    }
}