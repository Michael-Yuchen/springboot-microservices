package com.example.employee.repo;

import com.example.employee.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);
    
    Page<Employee> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    
    Page<Employee> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE " +
           "(:email IS NULL OR LOWER(e.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:departmentId IS NULL OR e.departmentId = :departmentId)")
    Page<Employee> findByFilters(@Param("email") String email, 
                                @Param("lastName") String lastName, 
                                @Param("departmentId") Long departmentId, 
                                Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchByNameOrEmail(@Param("query") String query);
    
    @Query("SELECT e.departmentId, COUNT(e) FROM Employee e GROUP BY e.departmentId")
    List<Object[]> countByDepartment();
}
