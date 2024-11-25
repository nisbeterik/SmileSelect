package com.smile_select.auth_service.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        // Log the error
        System.err.println("Access Denied: " + accessDeniedException.getMessage());
        
        // Set the response type and status
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        // Write the custom JSON response
        response.getWriter().write("{ \"error\": \"Access Denied\", \"message\": \"" + accessDeniedException.getMessage() + "\" }");
    }
}
