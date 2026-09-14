package com.sih.landacquisitionsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.sih.landacquisitionsystem.model.Project.Status;

@Entity
@Table(name = "acquisition_stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquisitionStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Project.Status stage;

    @ManyToOne
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private String remarks;
}