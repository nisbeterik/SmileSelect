package com.smile_select.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

public class UserResponseDTO {
    private String email;
    private String role;
    private String token; // JWT token
    private String password; // Used internally, hidden in responses
    private Long id;

    public UserResponseDTO(String email, String role, String token, String password, Long id) {
        this.email = email;
        this.role = role;
        this.token = token;
        this.password = password;
        this.id = id;
    }

    public UserResponseDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}