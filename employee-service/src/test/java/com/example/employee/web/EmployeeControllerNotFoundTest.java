package com.example.employee.web;

import com.example.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
class EmployeeControllerNotFoundTest {

    @Autowired MockMvc mvc;
    @MockBean EmployeeService service;

    @Test
    void byId_whenMissing_returns404_withProblemDetail() throws Exception {
        when(service.getById(999L, true)).thenThrow(new NoSuchElementException("not found"));

        mvc.perform(get("/api/v1/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }
}
