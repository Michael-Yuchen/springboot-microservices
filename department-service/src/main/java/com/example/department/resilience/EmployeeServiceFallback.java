package com.example.department.resilience;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback service for Employee Service when it's unavailable
 */
@Component
@Slf4j
public class EmployeeServiceFallback {

    public List<Object> getEmployeesByDepartmentFallback(Long departmentId, Exception ex) {
        log.warn("Employee service is unavailable, using fallback for department ID: {}", departmentId, ex);
        
        return Collections.emptyList();
    }

    public Integer getEmployeeCountByDepartmentFallback(Long departmentId, Exception ex) {
        log.warn("Employee service is unavailable, using fallback for department ID: {}", departmentId, ex);
        
        return 0;
    }
}
