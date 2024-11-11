package com.smile_select.smile_select_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Enables CORS for all paths
                        .allowedOrigins("http://localhost:8081") // Vue app URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Add any other HTTP methods you use
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
