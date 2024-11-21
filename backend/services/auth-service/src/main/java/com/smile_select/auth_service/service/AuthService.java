package com.smile_select.auth_service.service;

import com.smile_select.auth_service.dto.LoginRequest;
import com.smile_select.auth_service.dto.UserResponseDTO;
import com.smile_select.auth_service.exception.ResourceNotFoundException;
import com.smile_select.auth_service.mock.MockUserService;
import com.smile_select.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private MockUserService mockUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UserResponseDTO loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String role = loginRequest.getRole();

        System.out.println("Attempting login for email: " + email + ", role: " + role);

        // Retrieve mock user data
        UserResponseDTO userResponse = mockUserService.findUserByEmail(email, role);
        if (userResponse == null) {
            System.err.println("No user found for email: " + email + " and role: " + role);
            throw new ResourceNotFoundException("Invalid credentials for " + role);
        }

        // Verify password
        if (!passwordEncoder.matches(password, userResponse.getPassword())) {
            System.err.println("Password mismatch for email: " + email);
            throw new ResourceNotFoundException("Invalid credentials for " + role);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(email, role);
        userResponse.setToken(token);
        userResponse.setPassword(null); // Hide password in the response
        return userResponse;
    }
}
