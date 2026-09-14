package com.sih.landacquisitionsystem.dto;

import lombok.*;
import com.sih.landacquisitionsystem.model.Compression.Status;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompensationDTO {

    private Long id;
    private Long parcelId; // LandParcel ID
    private Long projectId; // Project ID
    private Long beneficiaryId; // LandOwner ID
    private Double assessedAmount;
    private Double approvedAmount;
    private Double paidAmount;
    private LocalDate paidDate;
    private String transactionReference;
    private com.sih.landacquisitionsystem.model.Compression.Status status;
}