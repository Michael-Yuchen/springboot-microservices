package com.example.department.messaging;

import com.example.department.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * Publisher for department events using Spring Cloud Stream
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DepartmentEventPublisher {

    private final StreamBridge streamBridge;

    public void publishDepartmentCreated(DepartmentCreatedEvent event) {
        try {
            streamBridge.send("departmentCreated-out-0", event);
            log.info("Published department created event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish department created event: {}", event, e);
        }
    }

    public void publishDepartmentUpdated(DepartmentUpdatedEvent event) {
        try {
            streamBridge.send("departmentUpdated-out-0", event);
            log.info("Published department updated event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish department updated event: {}", event, e);
        }
    }

    public void publishDepartmentDeleted(DepartmentDeletedEvent event) {
        try {
            streamBridge.send("departmentDeleted-out-0", event);
            log.info("Published department deleted event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish department deleted event: {}", event, e);
        }
    }
}
