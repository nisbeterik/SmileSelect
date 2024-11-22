package com.smile_select.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smile_select.logging_service.model.LogEntry;
import com.smile_select.logging_service.service.LogService;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService){
        this.logService = logService;
    }

    // Included for testing purposes. This endpoint will likely never be used because we create logs with MQTT, not through REST.
    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody LogEntry logEntry){
        LogEntry createdLogEntry = logService.save(logEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLogEntry);
    }
    
}
