package com.example.employee.web;

import com.example.employee.service.EmployeeService; // import your service
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

/**
 * 409 when DB uniqueness / FK constraints are violated.
 */
@WebMvcTest(controllers = EmployeeController.class)
class EmployeeControllerConflictTest {

    @Autowired MockMvc mvc;

    @MockBean EmployeeService employeeService;

    @Test
    void create_whenDuplicateEmail_returns409_withProblemDetail() throws Exception {
        // Adjust method name to your actual service API, e.g., create(...)/save(...):
        when(employeeService.create(any()))
                .thenThrow(new DataIntegrityViolationException("duplicate email"));

        String body = """
    { "firstName":"A","lastName":"B","email":"dup@example.com","departmentId":1 }
    """;

        mvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }
}
