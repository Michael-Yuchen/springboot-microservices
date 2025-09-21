package com.example.department.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "departments", schema = "department")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name is required")
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "code is required")
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(columnDefinition = "text")
    private String description;
}
