package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.model.AcquisitionCase;
import com.sih.landacquisitionsystem.service.AcquisitionCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acquisition-cases")
@RequiredArgsConstructor
public class AcquisitionCaseController {

    private final AcquisitionCaseService acquisitionCaseService;

    @GetMapping
    public List<AcquisitionCase> getAllAcquisitionCases() {
        return acquisitionCaseService.getAllAcquisitionCases();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcquisitionCase> getAcquisitionCaseById(@PathVariable Long id) {
        return acquisitionCaseService.getAcquisitionCaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/case-number/{caseNumber}")
    public ResponseEntity<AcquisitionCase> getAcquisitionCaseByCaseNumber(@PathVariable String caseNumber) {
        return acquisitionCaseService.getAcquisitionCaseByCaseNumber(caseNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AcquisitionCase createAcquisitionCase(@RequestBody AcquisitionCase acquisitionCase) {
        return acquisitionCaseService.createAcquisitionCase(acquisitionCase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcquisitionCase> updateAcquisitionCase(@PathVariable Long id, @RequestBody AcquisitionCase acquisitionCaseDetails) {
        return acquisitionCaseService.getAcquisitionCaseById(id)
                .map(acquisitionCase -> {
                    acquisitionCase.setCaseNumber(acquisitionCaseDetails.getCaseNumber());
                    acquisitionCase.setStatus(acquisitionCaseDetails.getStatus());
                    acquisitionCase.setClaimantName(acquisitionCaseDetails.getClaimantName());
                    acquisitionCase.setClaimantContactInfo(acquisitionCaseDetails.getClaimantContactInfo());
                    acquisitionCase.setLandParcel(acquisitionCaseDetails.getLandParcel());
                    acquisitionCase.setHandlerUser(acquisitionCaseDetails.getHandlerUser());
                    return ResponseEntity.ok(acquisitionCaseService.updateAcquisitionCase(acquisitionCase));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAcquisitionCase(@PathVariable Long id) {
        return acquisitionCaseService.getAcquisitionCaseById(id)
                .map(acquisitionCase -> {
                    acquisitionCaseService.deleteAcquisitionCase(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update the status of an acquisition case.
     * The changedBy user is extracted from the authenticated user's Firebase UID.
     *
     * @param id        The ID of the acquisition case
     * @param newStatus The new status to transition to
     * @param comments  Optional comments about the status change
     * @return The updated acquisition case
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AcquisitionCase> updateStatus(@PathVariable Long id,
                                                        @RequestParam String newStatus,
                                                        @RequestParam(required = false) String comments) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String firebaseUid = authentication.getName(); // Firebase UID set as principal in FirebaseAuthFilter
        AcquisitionCase updatedCase = acquisitionCaseService.updateStatus(id, newStatus, firebaseUid, comments);
        return ResponseEntity.ok(updatedCase);
    }
}