package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.CompensationDTO;
import com.sih.landacquisitionsystem.service.CompensationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compensations")
public class CompensationController {

    private final CompensationService service;

    public CompensationController(CompensationService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CompensationDTO> create(@RequestBody CompensationDTO dto) { return ResponseEntity.ok(service.createCompensation(dto)); }

    @GetMapping
    public ResponseEntity<List<CompensationDTO>> getAll() { return ResponseEntity.ok(service.getAllCompensations()); }

    @GetMapping("/{id}")
    public ResponseEntity<CompensationDTO> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getCompensationById(id)); }

    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<List<CompensationDTO>> byParcel(@PathVariable Long parcelId) { return ResponseEntity.ok(service.getCompensationsByParcelId(parcelId)); }

    @PutMapping("/{id}")
    public ResponseEntity<CompensationDTO> update(@PathVariable Long id, @RequestBody CompensationDTO dto) { return ResponseEntity.ok(service.updateCompensation(id, dto)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteCompensation(id); return ResponseEntity.noContent().build(); }
}
