package com.example.employee.service;

import com.example.employee.client.DepartmentClient;
import com.example.employee.domain.Employee;
import com.example.employee.dto.EmployeeDTO;
import com.example.employee.repo.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class EmployeeServiceTest {
    @Mock
    EmployeeRepository repository;
    @Mock
    DepartmentClient departmentClient;

    @InjectMocks
    EmployeeService service;

    @Test
    @DisplayName("getAll(): returns all employees from repository")
    void getAll_returns_all_employees() {
        // Given
        List<Employee> employees = List.of(
                Employee.builder().id(1L).firstName("Alice").lastName("Nguyen").email("alice@example.com").departmentId(1L).build(),
                Employee.builder().id(2L).firstName("Bob").lastName("Smith").email("bob@example.com").departmentId(2L).build()
        );
        when(repository.findAll()).thenReturn(employees);

        // When
        List<EmployeeDTO> result = service.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("alice@example.com");
        assertThat(result.get(1).getEmail()).isEqualTo("bob@example.com");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("getById(): returns employee when found")
    void getById_when_found_returns_employee() {
        // Given
        Employee employee = Employee.builder().id(1L).firstName("Alice").lastName("Nguyen").email("alice@example.com").departmentId(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        // When
        EmployeeDTO result = service.getById(1L, true);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("getById(): throws exception when not found")
    void getById_when_not_found_throws_exception() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getById(999L, true))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Employee not found");
        verify(repository).findById(999L);
    }

    @ParameterizedTest(name = "create({0}) → duplicate? {1}")
    @CsvSource({
            "dina@example.com, false",
            "alice@example.com, true"
    })
    @DisplayName("create(): throws when email exists; persists otherwise")
    void create_handles_duplicates(String email, boolean duplicate) {
        when(repository.existsByEmail(email)).thenReturn(duplicate);
        if (duplicate) {
            assertThatThrownBy(() -> service.create(EmployeeDTO.builder()
                    .firstName("X").lastName("Y").email(email).build()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already exists");
        } else {
            when(repository.save(any(Employee.class)))
                    .thenAnswer(inv -> { Employee e = inv.getArgument(0); e.setId(101L); return e; });
            var out = service.create(EmployeeDTO.builder()
                    .firstName("X").lastName("Y").email(email).build());
            assertThat(out.getId()).isEqualTo(101L);
        }
    }

}
