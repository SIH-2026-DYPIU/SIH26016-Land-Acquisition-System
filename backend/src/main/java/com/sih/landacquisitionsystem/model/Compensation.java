package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "compensations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compensation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parcel_id", nullable = false)
    private LandParcel parcel;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private LandOwner beneficiary;

    @Column(nullable = false)
    private Double assessedAmount;

    @Column(nullable = false)
    private Double approvedAmount;

    @Column(nullable = false)
    private Double paidAmount;

    private LocalDate paidDate;

    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        ASSESSED,
        APPROVED,
        PARTIALLY_PAID,
        PAID,
        DISPUTED
    }
}