package com.example.employee.web;

import com.example.employee.dto.EmployeeDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@DisplayName("EmployeeController Success Tests")
class EmployeeControllerSuccessTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EmployeeService service;

    @Nested
    class List_and_Get {
        @Test
        void list_returns_200_and_array() throws Exception {
            // Given
            List<EmployeeDTO> employees = List.of(
                    EmployeeDTO.builder().id(1L).firstName("Alice").lastName("Nguyen").email("alice@example.com").departmentId(1L).build(),
                    EmployeeDTO.builder().id(2L).firstName("Bob").lastName("Smith").email("bob@example.com").departmentId(2L).build()
            );
            when(service.getAll()).thenReturn(employees);

            // When & Then
            mvc.perform(get("/api/v1/employees"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].email").value("alice@example.com"))
                    .andExpect(jsonPath("$[1].email").value("bob@example.com"));
        }

        @Test
        void getById_returns_200_and_employee() throws Exception {
            // Given
            EmployeeDTO employee = EmployeeDTO.builder()
                    .id(1L)
                    .firstName("Alice")
                    .lastName("Nguyen")
                    .email("alice@example.com")
                    .departmentId(1L)
                    .build();
            when(service.getById(1L, true)).thenReturn(employee);

            // When & Then
            mvc.perform(get("/api/v1/employees/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("Alice"))
                    .andExpect(jsonPath("$.lastName").value("Nguyen"))
                    .andExpect(jsonPath("$.email").value("alice@example.com"))
                    .andExpect(jsonPath("$.departmentId").value(1));
        }
    }

    @Nested
    class Create {
        @Test
        void create_valid_returns_201() throws Exception {
            // Given
            EmployeeDTO input = EmployeeDTO.builder()
                    .firstName("Dina")
                    .lastName("Khan")
                    .email("dina@example.com")
                    .departmentId(1L)
                    .build();
            EmployeeDTO saved = EmployeeDTO.builder()
                    .id(10L)
                    .firstName("Dina")
                    .lastName("Khan")
                    .email("dina@example.com")
                    .departmentId(1L)
                    .build();
            when(service.create(any())).thenReturn(saved);

            // When & Then
            mvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.firstName").value("Dina"))
                    .andExpect(jsonPath("$.lastName").value("Khan"))
                    .andExpect(jsonPath("$.email").value("dina@example.com"))
                    .andExpect(jsonPath("$.departmentId").value(1));
        }
    }
}
