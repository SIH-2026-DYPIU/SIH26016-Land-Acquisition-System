package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.CompensationDTO;
import com.sih.landacquisitionsystem.service.CompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compensation")
public class CompensationController {

    @Autowired
    private CompensationService compensationService;

    @PostMapping
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY') or hasRole('DISTRICT_AUTHORITY')")
    public ResponseEntity<CompensationDTO> createCompensation(@RequestBody CompensationDTO compensationDTO) {
        CompensationDTO createdCompensation = compensationService.createCompensation(compensationDTO);
        return ResponseEntity.ok(createdCompensation);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompensationDTO> getCompensationById(@PathVariable Long id) {
        CompensationDTO compensationDTO = compensationService.getCompensationById(id);
        return ResponseEntity.ok(compensationDTO);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CompensationDTO>> getAllCompensations() {
        List<CompensationDTO> compensations = compensationService.getAllCompensations();
        return ResponseEntity.ok(compensations);
    }

    @GetMapping("/parcel/{parcelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CompensationDTO>> getCompensationsByParcelId(@PathVariable Long parcelId) {
        List<CompensationDTO> compensations = compensationService.getCompensationsByParcelId(parcelId);
        return ResponseEntity.ok(compensations);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY') or hasRole('DISTRICT_AUTHORITY')")
    public ResponseEntity<CompensationDTO> updateCompensation(@PathVariable Long id, @RequestBody CompensationDTO compensationDTO) {
        CompensationDTO updatedCompensation = compensationService.updateCompensation(id, compensationDTO);
        return ResponseEntity.ok(updatedCompensation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompensation(@PathVariable Long id) {
        compensationService.deleteCompensation(id);
        return ResponseEntity.noContent().build();
    }
}