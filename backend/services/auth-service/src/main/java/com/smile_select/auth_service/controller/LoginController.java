package main.java.com.smile_select.auth_service.controller;

import com.smile_select.auth_service.dto.LoginRequest;
import com.smile_select.auth_service.dto.UserResponseDTO;
import com.smile_select.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/login")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping()
    public ResponseEntity<UserResponseDTO> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        UserResponseDTO userResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(userResponse);
    }
}
