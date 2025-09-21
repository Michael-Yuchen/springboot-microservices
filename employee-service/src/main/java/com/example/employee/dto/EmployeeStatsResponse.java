package com.example.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeStatsResponse {
    private long totalEmployees;
    private Map<Long, Long> employeesByDepartment;
    private Map<String, Long> employeesByFirstName;
    private Map<String, Long> employeesByLastName;
}
