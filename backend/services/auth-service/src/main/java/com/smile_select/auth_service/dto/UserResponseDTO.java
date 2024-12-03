package com.smile_select.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}