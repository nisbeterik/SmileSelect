package com.smile_select.account_service.controller;

import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.model.Dentist;
import com.smile_select.account_service.service.PatientService;

import com.smile_select.account_service.dto.LoginRequest;

import com.smile_select.account_service.service.DentistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private DentistService dentistService;

    @PostMapping("/dentist")
    public ResponseEntity<?> loginDentist(@RequestBody LoginRequest loginRequest) {
    String email = loginRequest.getEmail();
    String password = loginRequest.getPassword();

    Optional<Dentist> dentist = dentistService.findDentistByEmail(email);
    if (dentist.isPresent() && dentistService.checkPassword(dentist.get(), password)) {
        return ResponseEntity.ok("Login Successful");
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
}
}