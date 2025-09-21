package com.example.employee.web;

import com.example.employee.dto.*;
import com.example.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@DisplayName("EmployeeController Expanded API Tests")
class EmployeeControllerExpandedTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EmployeeService service;

    @Nested
    class Paginated_Get {
        @Test
        void getAll_withPagination_returnsPageResponse() throws Exception {
            // Given
            PageResponse<EmployeeDTO> pageResponse = PageResponse.<EmployeeDTO>builder()
                    .content(List.of(
                            EmployeeDTO.builder().id(1L).firstName("Alice").lastName("Nguyen").email("alice@example.com").build()
                    ))
                    .page(0)
                    .size(20)
                    .totalElements(1)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();

            when(service.getAllPaginated(any(EmployeeSearchRequest.class))).thenReturn(pageResponse);

            // When & Then
            mvc.perform(get("/api/v1/employees")
                            .param("page", "0")
                            .param("size", "20")
                            .param("sort", "lastName,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void getAll_withFilters_returnsFilteredResults() throws Exception {
            // Given
            PageResponse<EmployeeDTO> pageResponse = PageResponse.<EmployeeDTO>builder()
                    .content(List.of())
                    .page(0)
                    .size(20)
                    .totalElements(0)
                    .totalPages(0)
                    .build();

            when(service.getAllPaginated(any(EmployeeSearchRequest.class))).thenReturn(pageResponse);

            // When & Then
            mvc.perform(get("/api/v1/employees")
                            .param("email", "test@example.com")
                            .param("lastName", "Smith")
                            .param("departmentId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    @Nested
    class Get_ById {
        @Test
        void getById_withEnrichment_returnsEmployeeWithDepartment() throws Exception {
            // Given
            EmployeeDTO employee = EmployeeDTO.builder()
                    .id(1L)
                    .firstName("Alice")
                    .lastName("Nguyen")
                    .email("alice@example.com")
                    .departmentId(1L)
                    .department(DepartmentDTO.builder().id(1L).name("IT").build())
                    .build();

            when(service.getById(1L, true)).thenReturn(employee);

            // When & Then
            mvc.perform(get("/api/v1/employees/1")
                            .param("enrich", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.department").isNotEmpty());
        }

        @Test
        void getById_withoutEnrichment_returnsEmployeeWithoutDepartment() throws Exception {
            // Given
            EmployeeDTO employee = EmployeeDTO.builder()
                    .id(1L)
                    .firstName("Alice")
                    .lastName("Nguyen")
                    .email("alice@example.com")
                    .departmentId(1L)
                    .build();

            when(service.getById(1L, false)).thenReturn(employee);

            // When & Then
            mvc.perform(get("/api/v1/employees/1")
                            .param("enrich", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.department").doesNotExist());
        }
    }

    @Nested
    class Update_Operations {
        @Test
        void update_returnsUpdatedEmployee() throws Exception {
            // Given
            EmployeeUpdateRequest request = EmployeeUpdateRequest.builder()
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated@example.com")
                    .departmentId(2L)
                    .build();

            EmployeeDTO updated = EmployeeDTO.builder()
                    .id(1L)
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated@example.com")
                    .departmentId(2L)
                    .build();

            when(service.update(1L, request)).thenReturn(updated);

            // When & Then
            mvc.perform(put("/api/v1/employees/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.email").value("updated@example.com"));
        }

        @Test
        void patch_returnsPartiallyUpdatedEmployee() throws Exception {
            // Given
            EmployeePatchRequest request = EmployeePatchRequest.builder()
                    .firstName("Patched")
                    .build();

            EmployeeDTO patched = EmployeeDTO.builder()
                    .id(1L)
                    .firstName("Patched")
                    .lastName("Original")
                    .email("original@example.com")
                    .build();

            when(service.patch(1L, request)).thenReturn(patched);

            // When & Then
            mvc.perform(patch("/api/v1/employees/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Patched"))
                    .andExpect(jsonPath("$.lastName").value("Original"));
        }
    }

    @Nested
    class Delete_Operation {
        @Test
        void delete_returns204() throws Exception {
            // Given
            // No return value for void method

            // When & Then
            mvc.perform(delete("/api/v1/employees/1"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class Search_Operation {
        @Test
        void search_returnsMatchingEmployees() throws Exception {
            // Given
            List<EmployeeDTO> results = List.of(
                    EmployeeDTO.builder().id(1L).firstName("Alice").lastName("Smith").email("alice@example.com").build()
            );

            when(service.search("alice")).thenReturn(results);

            // When & Then
            mvc.perform(get("/api/v1/employees/search")
                            .param("query", "alice"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].firstName").value("Alice"));
        }
    }

    @Nested
    class Stats_Operation {
        @Test
        void getStats_returnsStatistics() throws Exception {
            // Given
            EmployeeStatsResponse stats = EmployeeStatsResponse.builder()
                    .totalEmployees(10)
                    .employeesByDepartment(Map.of(1L, 5L, 2L, 5L))
                    .employeesByFirstName(Map.of("Alice", 3L, "Bob", 2L))
                    .employeesByLastName(Map.of("Smith", 4L, "Johnson", 3L))
                    .build();

            when(service.getStats()).thenReturn(stats);

            // When & Then
            mvc.perform(get("/api/v1/employees/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEmployees").value(10))
                    .andExpect(jsonPath("$.employeesByDepartment").isMap())
                    .andExpect(jsonPath("$.employeesByFirstName").isMap());
        }
    }

    @Nested
    class Bulk_Create {
        @Test
        void bulkCreate_returnsBulkResponse() throws Exception {
            // Given
            BulkCreateRequest request = BulkCreateRequest.builder()
                    .employees(List.of(
                            EmployeeDTO.builder().firstName("Alice").lastName("Smith").email("alice@example.com").build(),
                            EmployeeDTO.builder().firstName("Bob").lastName("Johnson").email("bob@example.com").build()
                    ))
                    .build();

            BulkCreateResponse response = BulkCreateResponse.builder()
                    .results(List.of(
                            BulkCreateResponse.BulkCreateResult.builder()
                                    .index(0)
                                    .success(true)
                                    .employee(EmployeeDTO.builder().id(1L).firstName("Alice").lastName("Smith").email("alice@example.com").build())
                                    .build()
                    ))
                    .totalProcessed(2)
                    .successful(1)
                    .failed(1)
                    .build();

            when(service.bulkCreate(request)).thenReturn(response);

            // When & Then
            mvc.perform(post("/api/v1/employees/bulkCreate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.totalProcessed").value(2))
                    .andExpect(jsonPath("$.successful").value(1))
                    .andExpect(jsonPath("$.failed").value(1))
                    .andExpect(jsonPath("$.results").isArray());
        }
    }
}
