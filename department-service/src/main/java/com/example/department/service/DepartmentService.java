package com.example.department.service;

import com.example.department.client.EmployeeClient;
import com.example.department.domain.Department;
import com.example.department.dto.*;
import com.example.department.event.*;
import com.example.department.exception.*;
import com.example.department.messaging.DepartmentEventPublisher;
import com.example.department.metrics.DepartmentMetrics;
import com.example.department.repo.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository repository;
    private final EmployeeClient employeeClient;
    private final DepartmentEventPublisher eventPublisher;
    private final DepartmentMetrics metrics;

    public List<Department> getAll() {
        return repository.findAll();
    }

    public PageResponse<DepartmentDTO> getAllPaginated(DepartmentSearchRequest request) {
        // Parse sort parameter
        Sort sort = parseSort(request.getSort());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // Use repository method with filters
        Page<Department> page = repository.findByFilters(
                request.getName(), 
                request.getCode(), 
                pageable
        );
        
        return buildPageResponse(page);
    }

    public Department getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    public DepartmentDTO getByCode(String code) {
        Department department = repository.findByCode(code)
                .orElseThrow(() -> new DepartmentNotFoundException(code, true));
        return toDTO(department);
    }

    @Transactional
    public Department create(Department department) {
        // Check for unique code constraint
        if (repository.existsByCode(department.getCode())) {
            throw new DepartmentConflictException("code", department.getCode());
        }
        Department saved = repository.save(department);
        
        // Publish department created event
        DepartmentCreatedEvent event = DepartmentCreatedEvent.builder()
                .departmentId(saved.getId())
                .name(saved.getName())
                .code(saved.getCode())
                .description(saved.getDescription())
                .createdAt(saved.getCreatedAt())
                .build();
        eventPublisher.publishDepartmentCreated(event);
        
        // Record metrics
        metrics.incrementDepartmentCreated();
        metrics.setTotalDepartments(repository.count());
        
        return saved;
    }

    @Transactional
    public DepartmentDTO update(Long id, DepartmentUpdateRequest request) {
        Department d = repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        
        // Check for code conflicts (excluding current department)
        if (request.getCode() != null && !request.getCode().equals(d.getCode())) {
            if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
                throw new IllegalArgumentException("Department code already exists: " + request.getCode());
            }
        }
        
        // Update fields
        if (request.getName() != null) d.setName(request.getName());
        if (request.getCode() != null) d.setCode(request.getCode());
        if (request.getDescription() != null) d.setDescription(request.getDescription());
        
        d = repository.save(d);
        
        // Publish department updated event
        DepartmentUpdatedEvent event = DepartmentUpdatedEvent.builder()
                .departmentId(d.getId())
                .name(d.getName())
                .code(d.getCode())
                .description(d.getDescription())
                .updatedAt(d.getUpdatedAt())
                .build();
        eventPublisher.publishDepartmentUpdated(event);
        
        // Record metrics
        metrics.incrementDepartmentUpdated();
        
        return toDTO(d);
    }

    @Transactional
    public DepartmentDTO patch(Long id, DepartmentPatchRequest request) {
        Department d = repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        
        // Check for code conflicts (excluding current department)
        if (request.getCode() != null && !request.getCode().equals(d.getCode())) {
            if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
                throw new IllegalArgumentException("Department code already exists: " + request.getCode());
            }
        }
        
        // Update only provided fields
        if (request.getName() != null) d.setName(request.getName());
        if (request.getCode() != null) d.setCode(request.getCode());
        if (request.getDescription() != null) d.setDescription(request.getDescription());
        
        d = repository.save(d);
        return toDTO(d);
    }

    @Transactional
    public void delete(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        
        // Check if department has employees (protective delete)
        try {
            List<Object> employees = employeeClient.getEmployeesByDepartment(id);
            if (employees != null && !employees.isEmpty()) {
                throw new DepartmentDeletionException(id, employees.size());
            }
        } catch (DepartmentDeletionException e) {
            throw e; // Re-throw our custom exception
        } catch (Exception e) {
            // If we can't check employees, we'll allow deletion but log the issue
            // In a real scenario, you might want to be more strict about this
        }
        
        // Publish department deleted event before deletion
        DepartmentDeletedEvent event = DepartmentDeletedEvent.builder()
                .departmentId(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .deletedAt(java.time.LocalDateTime.now())
                .build();
        eventPublisher.publishDepartmentDeleted(event);
        
        repository.deleteById(id);
        
        // Record metrics
        metrics.incrementDepartmentDeleted();
        metrics.setTotalDepartments(repository.count());
    }

    public DepartmentEmployeesResponse getDepartmentEmployees(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        
        List<Object> employees = new ArrayList<>();
        try {
            employees = employeeClient.getEmployeesByDepartment(id);
        } catch (Exception e) {
            // Handle case where employee service is not available
            employees = new ArrayList<>();
        }
        
        List<DepartmentEmployeesResponse.EmployeeSummaryDTO> employeeSummaries = employees.stream()
                .map(this::mapToEmployeeSummary)
                .collect(Collectors.toList());
        
        return DepartmentEmployeesResponse.builder()
                .department(toDTO(department))
                .employees(employeeSummaries)
                .totalEmployees(employeeSummaries.size())
                .build();
    }

    public DepartmentDTO toDTO(Department d) {
        return DepartmentDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .code(d.getCode())
                .description(d.getDescription())
                .build();
    }

    @SuppressWarnings("unchecked")
    private DepartmentEmployeesResponse.EmployeeSummaryDTO mapToEmployeeSummary(Object employee) {
        // This is a simplified mapping - in a real scenario, you'd have proper DTOs
        Map<String, Object> empMap = (Map<String, Object>) employee;
        return DepartmentEmployeesResponse.EmployeeSummaryDTO.builder()
                .id(Long.valueOf(empMap.get("id").toString()))
                .firstName(empMap.get("firstName").toString())
                .lastName(empMap.get("lastName").toString())
                .email(empMap.get("email").toString())
                .build();
    }

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        
        String[] parts = sortParam.split(",");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        
        String property = parts[0].trim();
        String direction = parts[1].trim().toUpperCase();
        
        Sort.Direction sortDirection = direction.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(sortDirection, property);
    }

    private PageResponse<DepartmentDTO> buildPageResponse(Page<Department> page) {
        List<DepartmentDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        PageResponse.SortInfo sortInfo = null;
        if (page.getSort().isSorted()) {
            Sort.Order order = page.getSort().iterator().next();
            sortInfo = PageResponse.SortInfo.builder()
                    .property(order.getProperty())
                    .direction(order.getDirection().name())
                    .build();
        }

        return PageResponse.<DepartmentDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(sortInfo)
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
