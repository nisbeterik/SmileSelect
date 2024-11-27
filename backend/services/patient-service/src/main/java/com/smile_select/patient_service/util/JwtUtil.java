package com.smile_select.patient_service.util;

import com.smile_select.patient_service.exception.JwtValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.smile_select.patient_service.config.JwtConfig;

import java.util.Date;

@Component
public class JwtUtil {

    private final JwtConfig jwtConfig;

    // Inject JwtConfig class
    @Autowired
    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getJwtExpirationInMs())) // Access expiration from JwtConfig
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            // Pass both message and cause (exception) to the JwtValidationException
            throw new JwtValidationException("Error extracting email from JWT: " + e.getMessage(), e);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (Exception e) {
            // Pass both message and cause (exception) to the JwtValidationException
            throw new JwtValidationException("Error extracting role from JWT: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // Pass both message and cause (exception) to the JwtValidationException
            throw new JwtValidationException("Token validation failed: " + e.getMessage(), e);
        }
    }
}
