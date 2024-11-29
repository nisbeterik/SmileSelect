package com.smile_select.auth_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile_select.auth_service.dto.LoginRequest;
import com.smile_select.auth_service.dto.UserResponseDTO;
import com.smile_select.auth_service.exception.ResourceNotFoundException;
import com.smile_select.auth_service.mqtt.MqttGateway;
import com.smile_select.auth_service.mqtt.MqttTopicHandler;
import com.smile_select.auth_service.util.JwtUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MqttGateway mqttGateway;

    private final MqttTopicHandler mqttTopicHandler;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${patient.service.url}")
    private String patientServiceUrl;

    @Value("${dentist.service.url}")
    private String dentistServiceUrl;

    public AuthService(MqttTopicHandler mqttTopicHandler) {
        this.mqttTopicHandler = mqttTopicHandler;
    }

    /**
     * Handles user login by validating the credentials and generating a JWT token.
     */
    public UserResponseDTO loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String role = loginRequest.getRole().toUpperCase();

        System.out.println("Attempting login for email: " + email + ", role: " + role);

        // Fetch user data from the appropriate service
        UserResponseDTO userResponse = fetchUserByEmail(email, role);

        // Check if the userResponseDTO is empty, i.e. no user with that email exists
        if (userResponse.getEmail() == null || userResponse.getPassword() == null) {
            System.err.println("No user data found for email: " + email);
            throw new ResourceNotFoundException("User not found for email: " + email);
        }

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

    private UserResponseDTO fetchUserByEmail(String email, String role) {

        // Generate random correlationId to keep track of the login request
        UUID correlationId = UUID.randomUUID();
        String topic = "";

        // Handle different roles 
        if (role.equals("PATIENT")) {
            topic = "/auth/login-patient/request";
        } else if (role.equals("DENTIST")) {
            topic = "/auth/login-dentist/request";
        }

        String payload = String.format("{\"correlationId\":\"%s\",\"email\":\"%s\"}",
                correlationId, email);

        // Publish login request
        System.out.println("Publishing login request under topic: " + topic);
        System.out.println("CorrelationId = " + correlationId);
        mqttGateway.publishMessage(payload, topic);

        CompletableFuture<String> future = mqttTopicHandler.registerPendingRequest(correlationId.toString());

        try {
            // Wait for the response
            String responsePayload = future.get(10, TimeUnit.SECONDS);

            // Parse the response (JSON string) into a Patient JSON object
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responsePayload);


            // Transform the patient JSON into a UserResponseDTO
            UserResponseDTO responseDTO = new UserResponseDTO();
            if (!responseJson.isEmpty() && responseJson.has("email") && responseJson.has("password")) {
                responseDTO.setEmail(responseJson.get("email").asText());
                responseDTO.setPassword(responseJson.get("password").asText());
                responseDTO.setRole(role);
            } 
            return responseDTO;
        } catch (TimeoutException e) {
            throw new IllegalStateException("Response timed out waiting for patient service.", e);
        } catch (Exception e) {
            throw new IllegalStateException("An error occurred while processing the login request.", e);
        }

    }
}
