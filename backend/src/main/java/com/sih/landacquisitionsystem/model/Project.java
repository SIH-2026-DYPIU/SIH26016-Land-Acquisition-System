package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    private Instant startDate;

    private Instant endDate;

    private BigDecimal budget;

    @Column(name = "project_status")
    @Enumerated(EnumType.STRING)
    private Status status; // e.g., NOTIFIED, AWARDED, COMPENSATION_PAID, POSSESSION_TAKEN, RR_COMPLETE

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LandParcel> landParcels;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

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
        NOTIFIED,
        AWARDED,
        COMPENSATION_PAID,
        POSSESSION_TAKEN,
        RR_COMPLETE
    }
}