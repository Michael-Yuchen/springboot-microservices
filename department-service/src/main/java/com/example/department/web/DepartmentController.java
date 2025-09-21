package com.example.department.web;

import com.example.department.domain.Department;
import com.example.department.dto.*;
import com.example.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

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
    public Department byId(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/by-code/{code}")
    public DepartmentDTO getByCode(@PathVariable String code) {
        return service.getByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Department create(@Valid @RequestBody Department d) {
        return service.create(d);
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
