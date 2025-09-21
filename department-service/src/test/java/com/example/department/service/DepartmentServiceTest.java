package com.example.department.service;

import com.example.department.domain.Department;
import com.example.department.repo.DepartmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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
class DepartmentServiceTest {

    @Mock
    DepartmentRepository repository;

    @InjectMocks
    DepartmentService service;

    @Test
    @DisplayName("getAll(): returns all departments from repository")
    void getAll_returns_all_departments() {
        // Given
        List<Department> departments = List.of(
                Department.builder().id(1L).name("IT").description("Technology").build(),
                Department.builder().id(2L).name("HR").description("Human Resources").build()
        );
        when(repository.findAll()).thenReturn(departments);

        // When
        List<Department> result = service.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("IT");
        assertThat(result.get(1).getName()).isEqualTo("HR");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("getById(): returns department when found")
    void getById_when_found_returns_department() {
        // Given
        Department department = Department.builder().id(1L).name("IT").description("Technology").build();
        when(repository.findById(1L)).thenReturn(Optional.of(department));

        // When
        Department result = service.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("IT");
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("getById(): throws exception when not found")
    void getById_when_not_found_throws_exception() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(RuntimeException.class);
        verify(repository).findById(999L);
    }

    @Test
    @DisplayName("create(): saves and returns department")
    void create_saves_and_returns_department() {
       
        Department input = Department.builder().name("Finance").description("Money things").build();
        Department saved = Department.builder().id(1L).name("Finance").description("Money things").build();
        when(repository.save(any(Department.class))).thenReturn(saved);

        
        Department result = service.create(input);

       
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Finance");
        verify(repository).save(input);
    }
}
