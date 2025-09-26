package com.example.employee.config;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Request condition for API version matching
 */
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    private final String version;

    public ApiVersionRequestCondition(String version) {
        this.version = version;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        return new ApiVersionRequestCondition(other.version);
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        String requestVersion = getVersionFromRequest(request);
        if (version.equals(requestVersion)) {
            return this;
        }
        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        return version.compareTo(other.version);
    }

    private String getVersionFromRequest(HttpServletRequest request) {
        // Check header first
        String version = request.getHeader("API-Version");
        if (version != null) {
            return version;
        }
        
        // Check Accept header
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("version=")) {
            String[] parts = accept.split("version=");
            if (parts.length > 1) {
                return parts[1].split(",")[0].trim();
            }
        }
        
        // Default to v1
        return "v1";
    }
}
