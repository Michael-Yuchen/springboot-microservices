package com.example.employee.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when an employee is deleted
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDeletedEvent {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private Long departmentId;
    private LocalDateTime deletedAt;
    @Builder.Default
    private String eventType = "EMPLOYEE_DELETED";
}
