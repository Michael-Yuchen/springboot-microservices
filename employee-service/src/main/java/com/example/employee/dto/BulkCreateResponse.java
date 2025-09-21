package com.example.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkCreateResponse {
    private List<BulkCreateResult> results;
    private int totalProcessed;
    private int successful;
    private int failed;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BulkCreateResult {
        private int index;
        private boolean success;
        private EmployeeDTO employee;
        private String error;
    }
}
