package com.smile_select.account_service.config;

import com.smile_select.account_service.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Password encoder for hashing passwords
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/api/accounts/login/**",
                                "/api/accounts/patients", // Allow POST registration for patients
                                "/api/accounts/dentists", // Allow POST registration for dentists
                                "/api/accounts/clinics" // Allow requests for clinics
                        ).permitAll()
                        // Protect specific GET endpoint for dentists by ID
                        .requestMatchers(HttpMethod.GET, "/api/accounts/dentists/{id}")
                        .authenticated()
                        // Allow all other endpoints under /api/accounts/dentists/**
                        .requestMatchers("/api/accounts/dentists/**")
                        .permitAll()
                        // Protect specific GET endpoint for patients by ID
                        .requestMatchers(HttpMethod.GET, "/api/accounts/patients/{id}")
                        .authenticated()
                        // Allow other patient-related routes
                        .requestMatchers("/api/accounts/patients/**")
                        .permitAll()
                        // Protect any other endpoints
                        .anyRequest()
                        .authenticated())
                .httpBasic(httpBasic -> httpBasic.disable());

        // Add JWT authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
