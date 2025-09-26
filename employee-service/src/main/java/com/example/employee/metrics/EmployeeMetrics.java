package com.example.employee.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Custom metrics for Employee Service
 */
@Component
@RequiredArgsConstructor
public class EmployeeMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter employeeCreatedCounter;
    private final Counter employeeUpdatedCounter;
    private final Counter employeeDeletedCounter;

    public EmployeeMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.employeeCreatedCounter = Counter.builder("employee.created")
                .description("Number of employees created")
                .register(meterRegistry);
        this.employeeUpdatedCounter = Counter.builder("employee.updated")
                .description("Number of employees updated")
                .register(meterRegistry);
        this.employeeDeletedCounter = Counter.builder("employee.deleted")
                .description("Number of employees deleted")
                .register(meterRegistry);
    }

    public void incrementEmployeeCreated() {
        employeeCreatedCounter.increment();
    }

    public void incrementEmployeeUpdated() {
        employeeUpdatedCounter.increment();
    }

    public void incrementEmployeeDeleted() {
        employeeDeletedCounter.increment();
    }

    public void setTotalEmployees(long count) {
        Gauge.builder("employee.total", () -> count)
                .description("Total number of employees")
                .register(meterRegistry);
    }
}
