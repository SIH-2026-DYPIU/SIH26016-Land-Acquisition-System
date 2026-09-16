package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.AcquisitionStageDTO;
import com.sih.landacquisitionsystem.service.AcquisitionStageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acquisition-stages")
public class AcquisitionStageController {

    private final AcquisitionStageService service;

    public AcquisitionStageController(AcquisitionStageService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<AcquisitionStageDTO> create(@RequestBody AcquisitionStageDTO dto) { return ResponseEntity.ok(service.createStage(dto)); }

    @GetMapping
    public ResponseEntity<List<AcquisitionStageDTO>> getAll() { return ResponseEntity.ok(service.getAllStages()); }

    @GetMapping("/{id}")
    public ResponseEntity<AcquisitionStageDTO> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getStageById(id)); }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<AcquisitionStageDTO> byProject(@PathVariable Long projectId) { return ResponseEntity.ok(service.getStageByProjectId(projectId)); }

    @PutMapping("/{id}")
    public ResponseEntity<AcquisitionStageDTO> update(@PathVariable Long id, @RequestBody AcquisitionStageDTO dto) { return ResponseEntity.ok(service.updateStage(id, dto)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteStage(id); return ResponseEntity.noContent().build(); }

    @PostMapping("/project/{projectId}/advance")
    public ResponseEntity<AcquisitionStageDTO> advance(@PathVariable Long projectId, @RequestParam Long updatedById, @RequestParam(defaultValue = "") String remarks) {
        return ResponseEntity.ok(service.advanceStage(projectId, updatedById, remarks));
    }
}
