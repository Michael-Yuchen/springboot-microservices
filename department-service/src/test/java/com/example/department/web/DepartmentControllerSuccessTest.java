package com.example.department.web;

import com.example.department.domain.Department;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@DisplayName("DepartmentController Success Tests")
class DepartmentControllerSuccessTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    DepartmentService service;

    @Nested
    class List_and_Get {
        @Test
        void list_returns_200_and_array() throws Exception {
            // Given
            List<Department> departments = List.of(
                    Department.builder().id(1L).name("IT").description("Technology").build(),
                    Department.builder().id(2L).name("HR").description("Human Resources").build()
            );
            when(service.getAll()).thenReturn(departments);

            // When & Then
            mvc.perform(get("/api/v1/departments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].name").value("IT"))
                    .andExpect(jsonPath("$[1].name").value("HR"));
        }

        @Test
        void getById_returns_200_and_department() throws Exception {
            // Given
            Department department = Department.builder().id(1L).name("IT").description("Technology").build();
            when(service.getById(1L)).thenReturn(department);

            // When & Then
            mvc.perform(get("/api/v1/departments/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("IT"))
                    .andExpect(jsonPath("$.description").value("Technology"));
        }
    }

    @Nested
    class Create {
        @Test
        void create_valid_returns_201() throws Exception {
            // Given
            Department input = Department.builder().name("Finance").description("Money things").build();
            Department saved = Department.builder().id(1L).name("Finance").description("Money things").build();
            when(service.create(any())).thenReturn(saved);

            // When & Then
            mvc.perform(post("/api/v1/departments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Finance"))
                    .andExpect(jsonPath("$.description").value("Money things"));
        }
    }
}
