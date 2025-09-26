package com.example.department.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a department is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCreatedEvent {
    private Long departmentId;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createdAt;
    @Builder.Default
    private String eventType = "DEPARTMENT_CREATED";
}
