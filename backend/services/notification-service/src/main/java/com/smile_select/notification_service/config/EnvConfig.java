package com.smile_select.notification_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@PropertySource("classpath:env.properties") // Loads the env.properties file from the classpath
@EnableConfigurationProperties
@EnableAsync
public class EnvConfig {

}