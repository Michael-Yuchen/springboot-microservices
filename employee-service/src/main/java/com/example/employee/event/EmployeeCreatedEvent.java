package com.example.employee.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when an employee is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreatedEvent {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private Long departmentId;
    private LocalDateTime createdAt;
    @Builder.Default
    private String eventType = "EMPLOYEE_CREATED";
}
