package com.example.department.exception;

/**
 * Exception thrown when department validation fails
 */
public class DepartmentValidationException extends RuntimeException {
    
    public DepartmentValidationException(String message) {
        super(message);
    }
    
    public DepartmentValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
