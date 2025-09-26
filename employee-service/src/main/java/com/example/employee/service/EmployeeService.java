package com.example.employee.service;

import com.example.employee.client.DepartmentClient;
import com.example.employee.domain.Employee;
import com.example.employee.dto.*;
import com.example.employee.event.*;
import com.example.employee.exception.*;
import com.example.employee.messaging.EmployeeEventPublisher;
import com.example.employee.metrics.EmployeeMetrics;
import com.example.employee.repo.EmployeeRepository;
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
public class EmployeeService {

    private final EmployeeRepository repository;
    private final DepartmentClient departmentClient;
    private final EmployeeEventPublisher eventPublisher;
    private final EmployeeMetrics metrics;

    public List<EmployeeDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PageResponse<EmployeeDTO> getAllPaginated(EmployeeSearchRequest request) {
        // Parse sort parameter
        Sort sort = parseSort(request.getSort());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // Use repository method with filters
        Page<Employee> page = repository.findByFilters(
                request.getEmail(), 
                request.getLastName(), 
                request.getDepartmentId(), 
                pageable
        );
        
        return buildPageResponse(page);
    }

    public EmployeeDTO getById(Long id, boolean enrichWithDepartment) {
        Employee e = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        return toDTO(e, enrichWithDepartment);
    }

    @Transactional
    public EmployeeDTO create(EmployeeDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new EmployeeConflictException("email", dto.getEmail());
        }
        Employee e = Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .departmentId(dto.getDepartmentId())
                .build();
        e = repository.save(e);
        
        // Publish employee created event
        EmployeeCreatedEvent event = EmployeeCreatedEvent.builder()
                .employeeId(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .departmentId(e.getDepartmentId())
                .createdAt(e.getCreatedAt())
                .build();
        eventPublisher.publishEmployeeCreated(event);
        
        // Record metrics
        metrics.incrementEmployeeCreated();
        metrics.setTotalEmployees(repository.count());
        
        return toDTO(e);
    }

    @Transactional
    public EmployeeDTO update(Long id, EmployeeUpdateRequest request) {
        Employee e = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        
        // Check for email conflicts (excluding current employee)
        if (request.getEmail() != null && !request.getEmail().equals(e.getEmail())) {
            if (repository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        
        // Update fields
        if (request.getFirstName() != null) e.setFirstName(request.getFirstName());
        if (request.getLastName() != null) e.setLastName(request.getLastName());
        if (request.getEmail() != null) e.setEmail(request.getEmail());
        if (request.getDepartmentId() != null) e.setDepartmentId(request.getDepartmentId());
        
        e = repository.save(e);
        
        // Publish employee updated event
        EmployeeUpdatedEvent event = EmployeeUpdatedEvent.builder()
                .employeeId(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .departmentId(e.getDepartmentId())
                .updatedAt(e.getUpdatedAt())
                .build();
        eventPublisher.publishEmployeeUpdated(event);
        
        // Record metrics
        metrics.incrementEmployeeUpdated();
        
        return toDTO(e);
    }

    @Transactional
    public EmployeeDTO patch(Long id, EmployeePatchRequest request) {
        Employee e = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        
        // Check for email conflicts (excluding current employee)
        if (request.getEmail() != null && !request.getEmail().equals(e.getEmail())) {
            if (repository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        
        // Update only provided fields
        if (request.getFirstName() != null) e.setFirstName(request.getFirstName());
        if (request.getLastName() != null) e.setLastName(request.getLastName());
        if (request.getEmail() != null) e.setEmail(request.getEmail());
        if (request.getDepartmentId() != null) e.setDepartmentId(request.getDepartmentId());
        
        e = repository.save(e);
        return toDTO(e);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        
        // Publish employee deleted event before deletion
        EmployeeDeletedEvent event = EmployeeDeletedEvent.builder()
                .employeeId(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartmentId())
                .deletedAt(java.time.LocalDateTime.now())
                .build();
        eventPublisher.publishEmployeeDeleted(event);
        
        repository.deleteById(id);
        
        // Record metrics
        metrics.incrementEmployeeDeleted();
        metrics.setTotalEmployees(repository.count());
    }

    public List<EmployeeDTO> search(String query) {
        List<Employee> employees = repository.searchByNameOrEmail(query);
        return employees.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EmployeeStatsResponse getStats() {
        long totalEmployees = repository.count();
        
        // Get department statistics
        List<Object[]> deptStats = repository.countByDepartment();
        Map<Long, Long> employeesByDepartment = deptStats.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
        
        // Get name statistics (first 10 most common)
        List<Employee> allEmployees = repository.findAll();
        Map<String, Long> employeesByFirstName = allEmployees.stream()
                .collect(Collectors.groupingBy(Employee::getFirstName, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        
        Map<String, Long> employeesByLastName = allEmployees.stream()
                .collect(Collectors.groupingBy(Employee::getLastName, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        
        return EmployeeStatsResponse.builder()
                .totalEmployees(totalEmployees)
                .employeesByDepartment(employeesByDepartment)
                .employeesByFirstName(employeesByFirstName)
                .employeesByLastName(employeesByLastName)
                .build();
    }

    @Transactional
    public BulkCreateResponse bulkCreate(BulkCreateRequest request) {
        List<BulkCreateResponse.BulkCreateResult> results = new ArrayList<>();
        int successful = 0;
        int failed = 0;
        
        for (int i = 0; i < request.getEmployees().size(); i++) {
            EmployeeDTO employeeDTO = request.getEmployees().get(i);
            try {
                // Check for duplicate email
                if (repository.existsByEmail(employeeDTO.getEmail())) {
                    results.add(BulkCreateResponse.BulkCreateResult.builder()
                            .index(i)
                            .success(false)
                            .error("Email already exists: " + employeeDTO.getEmail())
                            .build());
                    failed++;
                } else {
                    Employee e = Employee.builder()
                            .firstName(employeeDTO.getFirstName())
                            .lastName(employeeDTO.getLastName())
                            .email(employeeDTO.getEmail())
                            .departmentId(employeeDTO.getDepartmentId())
                            .build();
                    e = repository.save(e);
                    EmployeeDTO savedDTO = toDTO(e);
                    
                    results.add(BulkCreateResponse.BulkCreateResult.builder()
                            .index(i)
                            .success(true)
                            .employee(savedDTO)
                            .build());
                    successful++;
                }
            } catch (Exception ex) {
                results.add(BulkCreateResponse.BulkCreateResult.builder()
                        .index(i)
                        .success(false)
                        .error(ex.getMessage())
                        .build());
                failed++;
            }
        }
        
        return BulkCreateResponse.builder()
                .results(results)
                .totalProcessed(request.getEmployees().size())
                .successful(successful)
                .failed(failed)
                .build();
    }

    private EmployeeDTO toDTO(Employee e) {
        return toDTO(e, true);
    }

    private EmployeeDTO toDTO(Employee e, boolean enrichWithDepartment) {
        DepartmentDTO dept = null;
        if (enrichWithDepartment && e.getDepartmentId() != null) {
            try {
                dept = departmentClient.getDepartment(e.getDepartmentId());
            } catch (Exception ignored) { }
        }
        return EmployeeDTO.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .departmentId(e.getDepartmentId())
                .department(dept)
                .build();
    }

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "lastName");
        }
        
        String[] parts = sortParam.split(",");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.ASC, "lastName");
        }
        
        String property = parts[0].trim();
        String direction = parts[1].trim().toUpperCase();
        
        Sort.Direction sortDirection = direction.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(sortDirection, property);
    }

    private PageResponse<EmployeeDTO> buildPageResponse(Page<Employee> page) {
        List<EmployeeDTO> content = page.getContent().stream()
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

        return PageResponse.<EmployeeDTO>builder()
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
