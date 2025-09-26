package com.example.department.web.v2;

import com.example.department.annotation.ApiVersion;
import com.example.department.domain.Department;
import com.example.department.dto.*;
import com.example.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Department Controller V2 - Enhanced API with additional features
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@ApiVersion("v2")
public class DepartmentControllerV2 {

    private final DepartmentService service;

    @GetMapping
    public PageResponse<DepartmentDTO> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        
        DepartmentSearchRequest request = DepartmentSearchRequest.builder()
                .name(name)
                .code(code)
                .page(page)
                .size(size)
                .sort(sort)
                .build();
        
        return service.getAllPaginated(request);
    }

    @GetMapping("/{id}")
    public DepartmentDTO getById(@PathVariable Long id) {
        Department department = service.getById(id);
        return service.toDTO(department);
    }

    @GetMapping("/code/{code}")
    public DepartmentDTO getByCode(@PathVariable String code) {
        return service.getByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDTO create(@Valid @RequestBody Department department) {
        Department saved = service.create(department);
        return service.toDTO(saved);
    }

    @PutMapping("/{id}")
    public DepartmentDTO update(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}")
    public DepartmentDTO patch(@PathVariable Long id, @Valid @RequestBody DepartmentPatchRequest request) {
        return service.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/employees")
    public DepartmentEmployeesResponse getDepartmentEmployees(@PathVariable Long id) {
        return service.getDepartmentEmployees(id);
    }
}
