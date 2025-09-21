package com.example.department.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "EMPLOYEE-SERVICE")
public interface EmployeeClient {
    
    @GetMapping("/api/v1/employees")
    List<Object> getEmployeesByDepartment(@RequestParam Long departmentId);
}
