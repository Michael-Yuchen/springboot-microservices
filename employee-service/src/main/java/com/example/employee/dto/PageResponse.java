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
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private SortInfo sort;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SortInfo {
        private String property;
        private String direction;
    }
}
