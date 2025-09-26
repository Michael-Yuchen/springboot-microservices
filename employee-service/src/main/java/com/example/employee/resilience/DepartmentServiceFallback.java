package com.example.employee.resilience;

import com.example.employee.dto.DepartmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback service for Department Service when it's unavailable
 */
@Component
@Slf4j
public class DepartmentServiceFallback {

    public DepartmentDTO getDepartmentFallback(Long departmentId, Exception ex) {
        log.warn("Department service is unavailable, using fallback for department ID: {}", departmentId, ex);
        
        return DepartmentDTO.builder()
                .id(departmentId)
                .name("Department Unavailable")
                .code("UNAVAILABLE")
                .description("Department service is currently unavailable")
                .build();
    }

    public DepartmentDTO getDepartmentByCodeFallback(String code, Exception ex) {
        log.warn("Department service is unavailable, using fallback for department code: {}", code, ex);
        
        return DepartmentDTO.builder()
                .id(-1L)
                .name("Department Unavailable")
                .code(code)
                .description("Department service is currently unavailable")
                .build();
    }
}
