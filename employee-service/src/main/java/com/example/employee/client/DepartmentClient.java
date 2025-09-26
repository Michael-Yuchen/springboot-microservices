package com.example.employee.client;

import com.example.employee.dto.DepartmentDTO;
import com.example.employee.resilience.DepartmentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "DEPARTMENT-SERVICE", 
    path = "/api/v1/departments",
    fallback = DepartmentServiceFallback.class
)
public interface DepartmentClient {

    @GetMapping("/{id}")
    DepartmentDTO getDepartment(@PathVariable("id") Long id);
    
    @GetMapping("/code/{code}")
    DepartmentDTO getDepartmentByCode(@PathVariable("code") String code);
}
