package main.java.com.smile_select.auth_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs; // Expiration time in milliseconds

    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }
}
