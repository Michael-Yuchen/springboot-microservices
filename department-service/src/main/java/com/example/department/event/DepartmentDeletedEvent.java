package com.example.department.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a department is deleted
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDeletedEvent {
    private Long departmentId;
    private String name;
    private String code;
    private LocalDateTime deletedAt;
    @Builder.Default
    private String eventType = "DEPARTMENT_DELETED";
}
