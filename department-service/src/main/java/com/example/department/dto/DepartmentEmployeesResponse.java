package com.example.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEmployeesResponse {
    private DepartmentDTO department;
    private List<EmployeeSummaryDTO> employees;
    private int totalEmployees;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeSummaryDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
