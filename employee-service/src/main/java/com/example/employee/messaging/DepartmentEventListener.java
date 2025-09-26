package com.example.employee.messaging;

import com.example.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Event listener for department events in employee service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DepartmentEventListener {

    @SuppressWarnings("unused")
    private final EmployeeService employeeService;

    @Bean
    public Consumer<Object> departmentCreated() {
        return event -> {
            log.info("Received department created event: {}", event);
            // Here you could update employee department information, send notifications, etc.
        };
    }

    @Bean
    public Consumer<Object> departmentUpdated() {
        return event -> {
            log.info("Received department updated event: {}", event);
            // Handle department update in employee context
            // For example: update employee department information
        };
    }

    @Bean
    public Consumer<Object> departmentDeleted() {
        return event -> {
            log.info("Received department deleted event: {}", event);
            // Handle department deletion in employee context
            // For example: notify employees about department deletion
        };
    }
}
