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
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest LoginRequest){
        //Placeholder logic
        if("placeholderuser@mail.com".equals(loginRequest.getEmail())){

        }
    }

}
