package com.example.employee.exception;

/**
 * Exception thrown when there's an issue with department service communication
 */
public class DepartmentServiceException extends RuntimeException {
    
    public DepartmentServiceException(String message) {
        super(message);
    }
    
    public DepartmentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
