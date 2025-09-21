package com.example.employee.web;

import com.example.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = EmployeeController.class)
class EmployeeControllerValidationTest {

    @Autowired MockMvc mvc;
    @MockBean EmployeeService service; // mocked so validation happens before service is called

    @Test
    void create_whenEmailMissing_returns400_withProblemDetail() throws Exception {
        // Email intentionally omitted to trigger @Valid validation failure
        String body = """
      { "firstName":"Dina", "lastName":"Khan", "departmentId":1 }
    """;
        mvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
}