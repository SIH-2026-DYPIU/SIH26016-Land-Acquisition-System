package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.LandParcelDTO;
import com.sih.landacquisitionsystem.service.LandParcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcels")
public class LandParcelController {

    private final LandParcelService service;

    public LandParcelController(LandParcelService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<LandParcelDTO> create(@RequestBody LandParcelDTO dto) { return ResponseEntity.ok(service.createParcel(dto)); }

    @GetMapping
    public ResponseEntity<List<LandParcelDTO>> getAll() { return ResponseEntity.ok(service.getAllParcels()); }

    @GetMapping("/{id}")
    public ResponseEntity<LandParcelDTO> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getParcelById(id)); }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<LandParcelDTO>> byProject(@PathVariable Long projectId) { return ResponseEntity.ok(service.getParcelsByProjectId(projectId)); }

    @PutMapping("/{id}")
    public ResponseEntity<LandParcelDTO> update(@PathVariable Long id, @RequestBody LandParcelDTO dto) { return ResponseEntity.ok(service.updateParcel(id, dto)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteParcel(id); return ResponseEntity.noContent().build(); }
}
