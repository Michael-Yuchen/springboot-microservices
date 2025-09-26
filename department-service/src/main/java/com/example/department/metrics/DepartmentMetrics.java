package com.example.department.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Custom metrics for Department Service
 */
@Component
@RequiredArgsConstructor
public class DepartmentMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter departmentCreatedCounter;
    private final Counter departmentUpdatedCounter;
    private final Counter departmentDeletedCounter;

    public DepartmentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.departmentCreatedCounter = Counter.builder("department.created")
                .description("Number of departments created")
                .register(meterRegistry);
        this.departmentUpdatedCounter = Counter.builder("department.updated")
                .description("Number of departments updated")
                .register(meterRegistry);
        this.departmentDeletedCounter = Counter.builder("department.deleted")
                .description("Number of departments deleted")
                .register(meterRegistry);
    }

    public void incrementDepartmentCreated() {
        departmentCreatedCounter.increment();
    }

    public void incrementDepartmentUpdated() {
        departmentUpdatedCounter.increment();
    }

    public void incrementDepartmentDeleted() {
        departmentDeletedCounter.increment();
    }

    public void setTotalDepartments(long count) {
        Gauge.builder("department.total", () -> count)
                .description("Total number of departments")
                .register(meterRegistry);
    }
}
