package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "land_parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandParcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String parcelNumber;

    @Column(nullable = false)
    private String surveyNumber;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String district;

    private String village;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LandType landType;

    @Column(nullable = false)
    private Double area; // in hectares

    private Double latitude;  // for geo-tagging
    private Double longitude; // for geo-tagging

    @ManyToOne
    @JoinColumn(name = "land_owner_id")
    private LandOwner landOwner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LandParcelStatus status;

    public enum LandParcelStatus {
        IDENTIFIED,
        VERIFIED,
        NOTIFIED,
        VALUATED,
        AWARDED,
        COMPENSATED,
        POSSESSION_TAKEN,
        DISPUTED,
        REJECTED
    }
}