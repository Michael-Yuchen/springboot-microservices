package com.example.department.exception;

/**
 * Exception thrown when a department is not found
 */
public class DepartmentNotFoundException extends RuntimeException {
    
    public DepartmentNotFoundException(String message) {
        super(message);
    }
    
    public DepartmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DepartmentNotFoundException(Long id) {
        super("Department not found with id: " + id);
    }
    
    public DepartmentNotFoundException(String code, boolean byCode) {
        super("Department not found with code: " + code);
    }
}
