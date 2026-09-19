package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "acquisition_cases", uniqueConstraints = @UniqueConstraint(columnNames = "case_number"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquisitionCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_number", nullable = false)
    private String caseNumber;

    @Column(name = "case_status")
    private String status; // e.g., DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, COMPENSATED, CLOSED

    private String claimantName;

    private String claimantContactInfo; // phone, email, address

    @OneToOne
    @JoinColumn(name = "land_parcel_id", referencedColumnName = "id")
    private LandParcel landParcel;

    @OneToMany(mappedBy = "acquisitionCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    @OneToOne(mappedBy = "acquisitionCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private Compensation compensation;

    @OneToMany(mappedBy = "acquisitionCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusHistory> statusHistory;

    @OneToMany(mappedBy = "acquisitionCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    // Optional: handler user (officer handling the case)
    @ManyToOne
    @JoinColumn(name = "handler_user_id")
    private User handlerUser;

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