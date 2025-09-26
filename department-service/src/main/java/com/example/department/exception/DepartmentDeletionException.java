package com.example.department.exception;

/**
 * Exception thrown when department deletion fails due to existing employees
 */
public class DepartmentDeletionException extends RuntimeException {
    
    public DepartmentDeletionException(String message) {
        super(message);
    }
    
    public DepartmentDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DepartmentDeletionException(Long departmentId, int employeeCount) {
        super("Cannot delete department with id " + departmentId + 
              " because it has " + employeeCount + " employees");
    }
}
