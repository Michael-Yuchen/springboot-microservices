package com.example.employee.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkCreateRequest {
    @NotEmpty(message = "employees list cannot be empty")
    @Size(max = 100, message = "cannot create more than 100 employees at once")
    @Valid
    private List<EmployeeDTO> employees;
}
