package com.example.department.web;

import com.example.department.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 404 when resource is missing. */
@WebMvcTest(controllers = DepartmentController.class)
class DepartmentControllerNotFoundTest {
    @Autowired MockMvc mvc;
    @MockBean DepartmentService service;

    @Test
    void byId_whenMissing_returns404_withProblemDetail() throws Exception {
        when(service.getById(999L)).thenThrow(new RuntimeException("Department not found with id: 999"));
        mvc.perform(get("/api/v1/departments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }
}
