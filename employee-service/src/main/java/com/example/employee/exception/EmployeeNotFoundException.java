package com.example.employee.exception;

/**
 * Exception thrown when an employee is not found
 */
public class EmployeeNotFoundException extends RuntimeException {
    
    public EmployeeNotFoundException(String message) {
        super(message);
    }
    
    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}
