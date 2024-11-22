package com.smile_select.logging_service.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

enum LogLevel{
    INFO,
    WARN,
    ERROR;
}

@Document(collection = "logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    @Id
    @Field("_id")
    private String id;

    private LocalDateTime timestamp;
    private LogLevel logLevel;
    private String context;
    private String message;
    
}
