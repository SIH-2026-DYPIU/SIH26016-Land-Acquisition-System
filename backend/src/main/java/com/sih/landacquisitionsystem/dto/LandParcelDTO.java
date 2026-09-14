package com.sih.landacquisitionsystem.dto;

import lombok.*;
import com.sih.landacquisitionsystem.model.LandParcelStatus;
import com.sih.landacquisitionsystem.model.LandType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandParcelDTO {

    private Long id;
    private String parcelNumber;
    private String surveyNumber;
    private Long projectId; // Project ID
    private String state;
    private String district;
    private String village;
    private LandType landType;
    private Double area; // in hectares
    private Double latitude;
    private Double longitude;
    private Long landOwnerId; // LandOwner ID
    private LandParcelStatus status;
}