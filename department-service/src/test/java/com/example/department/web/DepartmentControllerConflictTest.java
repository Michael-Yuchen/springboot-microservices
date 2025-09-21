package com.example.department.web;

import com.example.department.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 409 when DB uniqueness or FK constraints are violated. */
@WebMvcTest(controllers = DepartmentController.class)
class DepartmentControllerConflictTest {
    @Autowired MockMvc mvc;
    @MockBean DepartmentService service;

    @Test
    void create_whenDuplicate_returns409_withProblemDetail() throws Exception {
        when(service.create(any())).thenThrow(new DataIntegrityViolationException("duplicate"));
        String body = """
    { "name": "Finance", "description": "Money things" }
    """;

        mvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }
}
