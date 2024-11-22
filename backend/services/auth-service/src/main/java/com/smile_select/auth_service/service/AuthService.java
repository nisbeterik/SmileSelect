package com.smile_select.auth_service.service;

import com.smile_select.auth_service.dto.LoginRequest;
import com.smile_select.auth_service.dto.UserResponseDTO;
import com.smile_select.auth_service.exception.ResourceNotFoundException;
import com.smile_select.auth_service.mock.MockUserService;
import com.smile_select.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${patient.service.url}")
    private String patientServiceUrl;

    @Value("${dentist.service.url}")
    private String dentistServiceUrl;

    public UserResponseDTO loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String role = loginRequest.getRole();

        System.out.println("Attempting login for email: " + email + ", role: " + role);

        // Determine which service to query based on the role
        String serviceUrl = getServiceUrlByRole(role);

        // Fetch user data from the appropriate service
        UserResponseDTO userResponse = fetchUserByEmail(serviceUrl, email, role);

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

    private String getServiceUrlByRole(String role) {
        if ("PATIENT".equalsIgnoreCase(role)) {
            return patientServiceUrl;
        } else if ("DENTIST".equalsIgnoreCase(role)) {
            return dentistServiceUrl;
        } else {
            throw new ResourceNotFoundException("Invalid role: " + role);
        }
    }

    private UserResponseDTO fetchUserByEmail(String serviceUrl, String email, String role) {
        String url = serviceUrl + "?email=" + email;
        try {
            return restTemplate.getForObject(url, UserResponseDTO.class);
        } catch (Exception e) {
            System.err.println("Error fetching user data from service: " + e.getMessage());
            throw new ResourceNotFoundException("No user found for email: " + email + " and role: " + role);
        }
    }
}
