package com.sih.landacquisitionsystem.dto;

import lombok.*;
import com.sih.landacquisitionsystem.model.Project.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquisitionStageDTO {

    private Long id;
    private Long projectId; // Project ID
    private Status stage;
    private Long updatedBy; // User ID
    private String remarks;
    // updatedAt is not typically needed in DTO for creation/update, but can be included if needed
}