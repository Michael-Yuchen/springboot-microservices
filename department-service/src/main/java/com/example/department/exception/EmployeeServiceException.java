package com.example.department.exception;

/**
 * Exception thrown when there's an issue with employee service communication
 */
public class EmployeeServiceException extends RuntimeException {
    
    public EmployeeServiceException(String message) {
        super(message);
    }
    
    public EmployeeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
