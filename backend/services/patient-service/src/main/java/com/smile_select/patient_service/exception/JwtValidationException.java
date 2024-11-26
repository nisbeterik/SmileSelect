package com.smile_select.patient_service.exception;

public class JwtValidationException extends RuntimeException {

    // Constructor accepting only a message
    public JwtValidationException(String message) {
        super(message);
    }

    // Constructor accepting a message and a cause
    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
