package com.example.department.messaging;

import com.example.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Event listener for employee events in department service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventListener {

    @SuppressWarnings("unused")
    private final DepartmentService departmentService;

    @Bean
    public Consumer<Object> employeeCreated() {
        return event -> {
            log.info("Received employee created event: {}", event);
            // Here you could update department statistics, send notifications, etc.
            // For example: update department employee count
        };
    }

    @Bean
    public Consumer<Object> employeeUpdated() {
        return event -> {
            log.info("Received employee updated event: {}", event);
            // Handle employee update in department context
        };
    }

    @Bean
    public Consumer<Object> employeeDeleted() {
        return event -> {
            log.info("Received employee deleted event: {}", event);
            // Handle employee deletion in department context
            // For example: update department employee count
        };
    }
}
