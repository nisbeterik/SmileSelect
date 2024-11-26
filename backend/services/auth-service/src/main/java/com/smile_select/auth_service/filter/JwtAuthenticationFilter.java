package com.smile_select.auth_service.filter;

import com.smile_select.auth_service.util.JwtUtil;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = jwtUtil.getEmailFromToken(token);
            }

            final String extractedToken = token; // Make token final for lambda usage

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(() -> jwtUtil.getRoleFromToken(extractedToken)) // Use final variable
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            handleError(response, "Invalid token signature. Please provide a valid token.",
                    HttpServletResponse.SC_UNAUTHORIZED);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            handleError(response, "Token has expired. Please log in again.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            handleError(response, "Authentication failed: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}