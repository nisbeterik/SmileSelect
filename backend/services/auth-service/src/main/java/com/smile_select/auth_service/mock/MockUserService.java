package com.smile_select.auth_service.mock;

import com.smile_select.auth_service.dto.UserResponseDTO;
import com.smile_select.auth_service.util.JwtUtil;
import com.smile_select.auth_service.dto.LoginRequest;
import com.smile_select.auth_service.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MockUserService {

    private final Map<String, UserResponseDTO> patients = Map.of(
            "patient1@mail.com", new UserResponseDTO("patient1@mail.com", "PATIENT", null,
                    "$2a$12$UwMJJ/A80xSdOFcQIPxmyOZZkdYT9kFxkJyILLCd70u9vJil5CARa") // Hashed "securepassword123"
    );

    private final Map<String, UserResponseDTO> dentists = Map.of(
            "dentist1@mail.com", new UserResponseDTO("dentist1@mail.com", "DENTIST", null, "$2a$10$examplehash1"),
            "dentist2@mail.com", new UserResponseDTO("dentist2@mail.com", "DENTIST", null, "$2b$10$examplehash2"));

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor-based dependency injection
    public MockUserService(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Login user and generate JWT token
    public UserResponseDTO loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String role = loginRequest.getRole();

        System.out.println("Login attempt: " + email + " Role: " + role);

        // Retrieve mock user data
        UserResponseDTO userResponse = findUserByEmail(email, role);
        if (userResponse == null) {
            System.out.println("User not found or role mismatch.");
            throw new ResourceNotFoundException("Invalid credentials for " + role);
        }

        // Verify password
        System.out.println("Hash from mock: " + userResponse.getPassword());
        System.out.println("Raw password: " + password);
        if (!passwordEncoder.matches(password, userResponse.getPassword())) {
            System.out.println("Password mismatch.");
            throw new ResourceNotFoundException("Invalid credentials for " + role);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(email, role);
        userResponse.setToken(token); // Add token to the response
        userResponse.setPassword(null); // Hide the password
        return userResponse;
    }

    // Find user by email and role
    public UserResponseDTO findUserByEmail(String email, String role) {
        if (role.equalsIgnoreCase("PATIENT")) {
            System.out.println("Looking for email in patients map: " + email);
            return patients.get(email);
        } else if (role.equalsIgnoreCase("DENTIST")) {
            System.out.println("Looking for email in dentists map: " + email);
            return dentists.get(email);
        }
        return null;
    }

}
