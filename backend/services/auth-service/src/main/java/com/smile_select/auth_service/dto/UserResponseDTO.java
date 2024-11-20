package main.java.com.smile_select.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private String email;
    private String role;
    private String token; // JWT token
    private String password; // Used internally, but will be hidden in responses
}
