package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.AffectedFamilyDTO;
import com.sih.landacquisitionsystem.service.AffectedFamilyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affected-families")
public class AffectedFamilyController {

    private final AffectedFamilyService service;

    public AffectedFamilyController(AffectedFamilyService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<AffectedFamilyDTO> create(@RequestBody AffectedFamilyDTO dto) { return ResponseEntity.ok(service.createAffectedFamily(dto)); }

    @GetMapping
    public ResponseEntity<List<AffectedFamilyDTO>> getAll() { return ResponseEntity.ok(service.getAllAffectedFamilies()); }

    @GetMapping("/{id}")
    public ResponseEntity<AffectedFamilyDTO> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getAffectedFamilyById(id)); }

    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<List<AffectedFamilyDTO>> byParcel(@PathVariable Long parcelId) { return ResponseEntity.ok(service.getAffectedFamiliesByParcelId(parcelId)); }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<AffectedFamilyDTO>> byProject(@PathVariable Long projectId) { return ResponseEntity.ok(service.getAffectedFamiliesByProjectId(projectId)); }

    @PutMapping("/{id}")
    public ResponseEntity<AffectedFamilyDTO> update(@PathVariable Long id, @RequestBody AffectedFamilyDTO dto) { return ResponseEntity.ok(service.updateAffectedFamily(id, dto)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteAffectedFamily(id); return ResponseEntity.noContent().build(); }
}
