package com.smile_select.auth_service.service;

import com.smile_select.auth_service.dto.LoginRequest;
import com.smile_select.auth_service.dto.UserResponseDTO;
import com.smile_select.auth_service.exception.ResourceNotFoundException;
import com.smile_select.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UserResponseDTO loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String role = loginRequest.getRole();

        // Determine the appropriate service based on role
        String serviceUrl = role.equalsIgnoreCase("PATIENT")
                ? "http://patient-service/api/patients?email=" + email
                : "http://dentist-service/api/dentists?email=" + email;

        // Fetch user details from the appropriate service
        UserResponseDTO userResponse = restTemplate.getForObject(serviceUrl, UserResponseDTO.class);
        if (userResponse == null || !passwordEncoder.matches(password, userResponse.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials for " + role);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(email, role);

        userResponse.setToken(token); // Add token to the response
        userResponse.setPassword(null); // Hide the password
        return userResponse;
    }
}
