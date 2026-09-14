package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.AcquisitionStageDTO;
import com.sih.landacquisitionsystem.service.AcquisitionStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class AcquisitionStageController {

    @Autowired
    private AcquisitionStageService acquisitionStageService;

    @GetMapping("/{projectId}/stage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AcquisitionStageDTO> getStageByProjectId(@PathVariable Long projectId) {
        AcquisitionStageDTO stageDTO = acquisitionStageService.getStageByProjectId(projectId);
        return ResponseEntity.ok(stageDTO);
    }

    @PutMapping("/{projectId}/stage")
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY') or hasRole('DISTRICT_AUTHORITY') or hasRole('PROJECT_AGENCY')")
    public ResponseEntity<AcquisitionStageDTO> updateStage(@PathVariable Long projectId, @RequestBody AcquisitionStageDTO stageDTO) {
        stageDTO.setProjectId(projectId);
        AcquisitionStageDTO updatedStage = acquisitionStageService.updateStage(stageDTO.getId(), stageDTO);
        return ResponseEntity.ok(updatedStage);
    }

    @PutMapping("/{projectId}/advance-stage")
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY') or hasRole('DISTRICT_AUTHORITY') or hasRole('PROJECT_AGENCY')")
    public ResponseEntity<AcquisitionStageDTO> advanceStage(
            @PathVariable Long projectId,
            @RequestParam Long updatedBy,
            @RequestParam(required = false) String remarks) {
        AcquisitionStageDTO updatedStage = acquisitionStageService.advanceStage(projectId, updatedBy, remarks);
        return ResponseEntity.ok(updatedStage);
    }

    @GetMapping("/stages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AcquisitionStageDTO>> getAllStages() {
        List<AcquisitionStageDTO> stages = acquisitionStageService.getAllStages();
        return ResponseEntity.ok(stages);
    }
}