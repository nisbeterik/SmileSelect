package main.java.com.smile_select.account_service.controller;

import com.smile_select.account_service.model.Patient;
import com.smile_select.account_service.model.Dentist;
import com.smile_select.account_service.service.PatientService;
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

    @PostMaping("/dentist")
    public ReponseEntity<?> loginDentist(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<main.java.com.smile_select.account_service.model.Dentist> dentist = dentistService
                .findDentistByEmail(email);
        if (dentist.isPresent() && dentist.get().getPassword().equals(password)) {
            return ResponseEntity.ok("Login Successful");
        }
        return ReponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
