package main.java.com.smile_select.auth_service.mock;

import com.smile_select.auth_service.dto.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MockUserService {

    private final Map<String, UserResponseDTO> patients = Map.of(
            "patient1@mail.com", new UserResponseDTO("patient1@mail.com", "PATIENT", null, "$2b$10$hashedPassword1"),
            "patient2@mail.com", new UserResponseDTO("patient2@mail.com", "PATIENT", null, "$2b$10$hashedPassword2")
    );

    private final Map<String, UserResponseDTO> dentists = Map.of(
            "dentist1@mail.com", new UserResponseDTO("dentist1@mail.com", "DENTIST", null, "$2b$10$hashedPassword3"),
            "dentist2@mail.com", new UserResponseDTO("dentist2@mail.com", "DENTIST", null, "$2b$10$hashedPassword4")
    );

    public UserResponseDTO findUserByEmail(String email, String role) {
        if (role.equalsIgnoreCase("PATIENT")) {
            return patients.get(email);
        } else if (role.equalsIgnoreCase("DENTIST")) {
            return dentists.get(email);
        }
        return null;
    }
}
