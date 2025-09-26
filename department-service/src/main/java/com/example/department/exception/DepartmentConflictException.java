package com.example.department.exception;

/**
 * Exception thrown when there's a conflict with department data (e.g., duplicate code)
 */
public class DepartmentConflictException extends RuntimeException {
    
    public DepartmentConflictException(String message) {
        super(message);
    }
    
    public DepartmentConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DepartmentConflictException(String field, String value) {
        super("Department already exists with " + field + ": " + value);
    }
}
