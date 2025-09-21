package com.example.department.web;

import com.example.department.client.EmployeeClient;
import com.example.department.dto.*;
import com.example.department.service.DepartmentService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@DisplayName("DepartmentController Expanded API Tests")
class DepartmentControllerExpandedTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    DepartmentService service;

    @MockBean
    EmployeeClient employeeClient;

    @Nested
    class Paginated_Get {
        @Test
        void getAll_withPagination_returnsPageResponse() throws Exception {
            // Given
            PageResponse<DepartmentDTO> pageResponse = PageResponse.<DepartmentDTO>builder()
                    .content(List.of(
                            DepartmentDTO.builder().id(1L).name("IT").code("IT001").description("Technology").build()
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

            when(service.getAllPaginated(any(DepartmentSearchRequest.class))).thenReturn(pageResponse);

            // When & Then
            mvc.perform(get("/api/v1/departments")
                            .param("page", "0")
                            .param("size", "20")
                            .param("sort", "name,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void getAll_withFilters_returnsFilteredResults() throws Exception {
            // Given
            PageResponse<DepartmentDTO> pageResponse = PageResponse.<DepartmentDTO>builder()
                    .content(List.of())
                    .page(0)
                    .size(20)
                    .totalElements(0)
                    .totalPages(0)
                    .build();

            when(service.getAllPaginated(any(DepartmentSearchRequest.class))).thenReturn(pageResponse);

            // When & Then
            mvc.perform(get("/api/v1/departments")
                            .param("name", "IT")
                            .param("code", "IT001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    @Nested
    class Get_By_Code {
        @Test
        void getByCode_returnsDepartment() throws Exception {
            // Given
            DepartmentDTO department = DepartmentDTO.builder()
                    .id(1L)
                    .name("IT")
                    .code("IT001")
                    .description("Technology")
                    .build();

            when(service.getByCode("IT001")).thenReturn(department);

            // When & Then
            mvc.perform(get("/api/v1/departments/by-code/IT001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("IT"))
                    .andExpect(jsonPath("$.code").value("IT001"));
        }
    }

    @Nested
    class Update_Operations {
        @Test
        void update_returnsUpdatedDepartment() throws Exception {
            // Given
            DepartmentUpdateRequest request = DepartmentUpdateRequest.builder()
                    .name("Updated IT")
                    .code("IT002")
                    .description("Updated Technology")
                    .build();

            DepartmentDTO updated = DepartmentDTO.builder()
                    .id(1L)
                    .name("Updated IT")
                    .code("IT002")
                    .description("Updated Technology")
                    .build();

            when(service.update(1L, request)).thenReturn(updated);

            // When & Then
            mvc.perform(put("/api/v1/departments/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated IT"))
                    .andExpect(jsonPath("$.code").value("IT002"));
        }

        @Test
        void patch_returnsPartiallyUpdatedDepartment() throws Exception {
            // Given
            DepartmentPatchRequest request = DepartmentPatchRequest.builder()
                    .name("Patched IT")
                    .build();

            DepartmentDTO patched = DepartmentDTO.builder()
                    .id(1L)
                    .name("Patched IT")
                    .code("IT001")
                    .description("Original Technology")
                    .build();

            when(service.patch(1L, request)).thenReturn(patched);

            // When & Then
            mvc.perform(patch("/api/v1/departments/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Patched IT"))
                    .andExpect(jsonPath("$.code").value("IT001"));
        }
    }

    @Nested
    class Delete_Operation {
        @Test
        void delete_returns204() throws Exception {
            // Given
            // No return value for void method

            // When & Then
            mvc.perform(delete("/api/v1/departments/1"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class Department_Employees {
        @Test
        void getDepartmentEmployees_returnsEmployeesList() throws Exception {
            // Given
            DepartmentEmployeesResponse response = DepartmentEmployeesResponse.builder()
                    .department(DepartmentDTO.builder().id(1L).name("IT").code("IT001").build())
                    .employees(List.of(
                            DepartmentEmployeesResponse.EmployeeSummaryDTO.builder()
                                    .id(1L)
                                    .firstName("Alice")
                                    .lastName("Smith")
                                    .email("alice@example.com")
                                    .build()
                    ))
                    .totalEmployees(1)
                    .build();

            when(service.getDepartmentEmployees(1L)).thenReturn(response);

            // When & Then
            mvc.perform(get("/api/v1/departments/1/employees"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.department.name").value("IT"))
                    .andExpect(jsonPath("$.employees").isArray())
                    .andExpect(jsonPath("$.totalEmployees").value(1))
                    .andExpect(jsonPath("$.employees[0].firstName").value("Alice"));
        }
    }
}
