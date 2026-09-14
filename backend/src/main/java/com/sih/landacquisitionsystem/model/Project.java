package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String projectCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectType projectType;

    @Column(nullable = false)
    private String ministry;

    @Column(nullable = false)
    private String implementingAgency;

    @Column(nullable = false)
    private String acquiringAuthority;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String district;

    private String village;

    private Double proposedArea; // in hectares

    private Double acquiredArea; // in hectares

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate expectedCompletionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        DRAFT,
        SUBMITTED,
        UNDER_SCRUTINY,
        APPROVED,
        LAND_IDENTIFICATION,
        NOTIFICATION,
        AWARD,
        COMPENSATION,
        POSSESSION,
        R_AND_R,
        COMPLETED,
        REJECTED
    }
}