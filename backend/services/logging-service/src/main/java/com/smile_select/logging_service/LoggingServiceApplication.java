package com.smile_select.logging_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.smile_select")
public class LoggingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggingServiceApplication.class, args);
	}

}
