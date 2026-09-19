package com.sih.landacquisitionsystem.dto;

import lombok.*;
import com.sih.landacquisitionsystem.model.Project.Status;
import com.sih.landacquisitionsystem.model.ProjectType;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {

    private Long id;
    private String projectCode;
    private String name;
    private String description;
    private ProjectType projectType;
    private String ministry;
    private String implementingAgency;
    private String acquiringAuthority;
    private String state;
    private String district;
    private String village;
    private Double proposedArea;
    private Double acquiredArea;
    private LocalDate startDate;
    private LocalDate expectedCompletionDate;
    private Status status;
    private Long createdBy; // User ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}