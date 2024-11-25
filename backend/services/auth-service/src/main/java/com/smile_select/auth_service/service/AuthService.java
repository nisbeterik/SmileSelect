package com.smile_select.auth_service.service;

import com.smile_select.auth_service.dto.LoginRequest;
import com.smile_select.auth_service.dto.UserResponseDTO;
import com.smile_select.auth_service.exception.ResourceNotFoundException;
import com.smile_select.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    /**
     * Handles user login by validating the credentials and generating a JWT token.
     */
    public UserResponseDTO loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String role = loginRequest.getRole().toUpperCase();

        System.out.println("Attempting login for email: " + email + ", role: " + role);

        // Determine the correct service URL based on the role
        String serviceUrl = getServiceUrlByRole(role);

        // Fetch user data from the appropriate service
        UserResponseDTO userResponse = fetchUserByEmail(serviceUrl, email);

        // Verify password
        if (!passwordEncoder.matches(password, userResponse.getPassword())) {
            System.err.println("Password mismatch for email: " + email);
            throw new ResourceNotFoundException("Invalid credentials for " + role);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(email, role);

        // Hide sensitive information
        userResponse.setToken(token);
        userResponse.setPassword(null); // Do not expose password in the response

        return userResponse;
    }

    /**
     * Determines the service URL based on the user's role.
     */
    private String getServiceUrlByRole(String role) {
        switch (role) {
            case "PATIENT":
                return patientServiceUrl;
            case "DENTIST":
                return dentistServiceUrl;
            default:
                throw new ResourceNotFoundException("Invalid role: " + role);
        }
    }

    /**
     * Fetches user details by email from the respective service (Patient or
     * Dentist).
     */
    private UserResponseDTO fetchUserByEmail(String serviceUrl, String email) {
        String url = serviceUrl + "/email/" + email; // e.g., http://localhost:8085/api/patients/email/{email}
        System.out.println("Calling service URL: " + url);

        try {
            return restTemplate.getForObject(url, UserResponseDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            // Handle 404 errors from the service
            throw new ResourceNotFoundException("No user found for email: " + email);
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("Error during REST call: " + e.getMessage());
            throw new ResourceNotFoundException("Failed to fetch user from service: " + e.getMessage());
        }
    }
}
