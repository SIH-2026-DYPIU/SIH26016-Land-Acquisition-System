package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.AffectedFamilyDTO;
import com.sih.landacquisitionsystem.service.AffectedFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affected-families")
public class AffectedFamilyController {

    @Autowired
    private AffectedFamilyService affectedFamilyService;

    @PostMapping
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY') or hasRole('DISTRICT_AUTHORITY')")
    public ResponseEntity<AffectedFamilyDTO> createAffectedFamily(@RequestBody AffectedFamilyDTO affectedFamilyDTO) {
        AffectedFamilyDTO created = affectedFamilyService.createAffectedFamily(affectedFamilyDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AffectedFamilyDTO> getAffectedFamilyById(@PathVariable Long id) {
        AffectedFamilyDTO affectedFamily = affectedFamilyService.getAffectedFamilyById(id);
        return ResponseEntity.ok(affectedFamily);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AffectedFamilyDTO>> getAllAffectedFamilies() {
        List<AffectedFamilyDTO> families = affectedFamilyService.getAllAffectedFamilies();
        return ResponseEntity.ok(families);
    }

    @GetMapping("/parcel/{parcelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AffectedFamilyDTO>> getAffectedFamiliesByParcelId(@PathVariable Long parcelId) {
        List<AffectedFamilyDTO> families = affectedFamilyService.getAffectedFamiliesByParcelId(parcelId);
        return ResponseEntity.ok(families);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AffectedFamilyDTO>> getAffectedFamiliesByProjectId(@PathVariable Long projectId) {
        List<AffectedFamilyDTO> families = affectedFamilyService.getAffectedFamiliesByProjectId(projectId);
        return ResponseEntity.ok(families);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY') or hasRole('DISTRICT_AUTHORITY')")
    public ResponseEntity<AffectedFamilyDTO> updateAffectedFamily(@PathVariable Long id, @RequestBody AffectedFamilyDTO affectedFamilyDTO) {
        AffectedFamilyDTO updated = affectedFamilyService.updateAffectedFamily(id, affectedFamilyDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAffectedFamily(@PathVariable Long id) {
        affectedFamilyService.deleteAffectedFamily(id);
        return ResponseEntity.noContent().build();
    }
}