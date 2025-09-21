package com.example.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentSearchRequest {
    private String name;
    private String code;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
    @Builder.Default
    private String sort = "name,asc";
}
