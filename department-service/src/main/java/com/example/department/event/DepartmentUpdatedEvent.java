package com.example.department.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a department is updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentUpdatedEvent {
    private Long departmentId;
    private String name;
    private String code;
    private String description;
    private LocalDateTime updatedAt;
    @Builder.Default
    private String eventType = "DEPARTMENT_UPDATED";
}
