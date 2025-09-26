package com.example.employee.messaging;

import com.example.employee.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * Publisher for employee events using Spring Cloud Stream
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventPublisher {

    private final StreamBridge streamBridge;

    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        try {
            streamBridge.send("employeeCreated-out-0", event);
            log.info("Published employee created event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish employee created event: {}", event, e);
        }
    }

    public void publishEmployeeUpdated(EmployeeUpdatedEvent event) {
        try {
            streamBridge.send("employeeUpdated-out-0", event);
            log.info("Published employee updated event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish employee updated event: {}", event, e);
        }
    }

    public void publishEmployeeDeleted(EmployeeDeletedEvent event) {
        try {
            streamBridge.send("employeeDeleted-out-0", event);
            log.info("Published employee deleted event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish employee deleted event: {}", event, e);
        }
    }
}
