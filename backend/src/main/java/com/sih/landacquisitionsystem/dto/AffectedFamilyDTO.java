package com.sih.landacquisitionsystem.dto;

import lombok.*;
import com.sih.landacquisitionsystem.model.RRStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffectedFamilyDTO {

    private Long id;
    private String familyIdentifier;
    private Long parcelId; // LandParcel ID
    private Long projectId; // Project ID
    private Integer numberOfMembers;
    private Boolean displacedStatus;
    private Boolean eligibility;
    private RRStatus rrStatus;
}