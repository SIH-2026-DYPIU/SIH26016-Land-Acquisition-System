package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "affected_families")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffectedFamily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String familyIdentifier;

    @ManyToOne
    @JoinColumn(name = "parcel_id", nullable = false)
    private LandParcel parcel;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Integer numberOfMembers;

    @Column(nullable = false)
    private Boolean displacedStatus;

    @Column(nullable = false)
    private Boolean eligibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RRStatus rrStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}