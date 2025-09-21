package com.example.department.web;

import com.example.department.service.DepartmentService; // mock the injected dependency
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.context.annotation.Import; // uncomment if you need to import your advice
// import com.example.department.web.GlobalExceptionHandler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web slice test to verify standardized 400 ProblemDetail response
 * when required fields are missing.
 *
 * NOTE:
 * - We mock DepartmentRepository because DepartmentController injects it.
 * - If your GlobalExceptionHandler is not auto-detected, add:
 *   @Import(GlobalExceptionHandler.class)
 */
@WebMvcTest(controllers = DepartmentController.class)
// @Import(GlobalExceptionHandler.class)
class DepartmentControllerValidationTest {

    @Autowired MockMvc mvc;

    @MockBean DepartmentService service; // must match the controller's injected type

    @Test
    void create_whenNameMissing_returns400_withProblemDetail() throws Exception {
        // Text blocks require a newline after the opening triple quotes
        String body = """
    { "description": "Money things" }
    """;

        mvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
}
