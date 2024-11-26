package com.smile_select.logging_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smile_select.logging_service.model.LogEntry;
import com.smile_select.logging_service.repository.LogRepository;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository; 

    public LogEntry save(LogEntry logEntry) {
        return logRepository.save(logEntry);  
    }
}
