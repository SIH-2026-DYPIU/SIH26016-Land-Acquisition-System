package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;

@Entity
@Table(name = "status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status; // e.g., DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, COMPENSATED, CLOSED

    @ManyToOne
    @JoinColumn(name = "changed_by_user_id")
    private User changedBy;

    @Column(name = "changed_at")
    private Instant changedAt;

    private String comments;

    @ManyToOne
    @JoinColumn(name = "acquisition_case_id")
    private AcquisitionCase acquisitionCase;

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
}