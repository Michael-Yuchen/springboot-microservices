package com.example.employee.web;

import com.example.employee.dto.*;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public PageResponse<EmployeeDTO> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort) {
        
        EmployeeSearchRequest request = EmployeeSearchRequest.builder()
                .email(email)
                .lastName(lastName)
                .departmentId(departmentId)
                .page(page)
                .size(size)
                .sort(sort)
                .build();
        
        return service.getAllPaginated(request);
    }

    @GetMapping("/{id}")
    public EmployeeDTO getById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean enrich) {
        return service.getById(id, enrich);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO create(@Valid @RequestBody EmployeeDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EmployeeDTO update(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}")
    public EmployeeDTO patch(@PathVariable Long id, @Valid @RequestBody EmployeePatchRequest request) {
        return service.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/search")
    public List<EmployeeDTO> search(@RequestParam String query) {
        return service.search(query);
    }

    @GetMapping("/stats")
    public EmployeeStatsResponse getStats() {
        return service.getStats();
    }

    @PostMapping("/bulkCreate")
    @ResponseStatus(HttpStatus.CREATED)
    public BulkCreateResponse bulkCreate(@Valid @RequestBody BulkCreateRequest request) {
        return service.bulkCreate(request);
    }
}
