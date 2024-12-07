package com.smile_select.dentist_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:env.properties") // Loads the env.properties file from the classpath
public class EnvConfig {
}
