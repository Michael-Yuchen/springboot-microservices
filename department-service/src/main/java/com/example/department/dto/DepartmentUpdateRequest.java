package com.example.department.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUpdateRequest {
    @Size(max = 120)
    private String name;

    @Size(max = 20)
    private String code;

    private String description;
}
