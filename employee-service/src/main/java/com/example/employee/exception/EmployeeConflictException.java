package com.example.employee.exception;

/**
 * Exception thrown when there's a conflict with employee data (e.g., duplicate email)
 */
public class EmployeeConflictException extends RuntimeException {
    
    public EmployeeConflictException(String message) {
        super(message);
    }
    
    public EmployeeConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EmployeeConflictException(String field, String value) {
        super("Employee already exists with " + field + ": " + value);
    }
}
