package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "compensations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compensation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount; // in local currency

    private String currency; // e.g., INR, USD

    private String compensationType; // e.g., LAND_VALUE, STRUCTURE_VALUE, SOLATIUM, etc.

    @Column(name = "payment_status")
    private String paymentStatus; // e.g., PENDING, PAID, PARTIAL, FAILED

    public enum Status {
        PENDING,
        PAID,
        PARTIAL,
        FAILED
    }

    private Instant paymentDate;

    @OneToOne
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