package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.model.LandParcel;
import com.sih.landacquisitionsystem.service.LandParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/land-parcels")
@RequiredArgsConstructor
public class LandParcelController {

    private final LandParcelService landParcelService;

    @GetMapping
    public List<LandParcel> getAllLandParcels() {
        return landParcelService.getAllLandParcels();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LandParcel> getLandParcelById(@PathVariable Long id) {
        return landParcelService.getLandParcelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public LandParcel createLandParcel(@RequestBody LandParcel landParcel) {
        return landParcelService.createLandParcel(landParcel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LandParcel> updateLandParcel(@PathVariable Long id, @RequestBody LandParcel landParcelDetails) {
        return landParcelService.getLandParcelById(id)
                .map(landParcel -> {
                    landParcel.setParcelNumber(landParcelDetails.getParcelNumber());
                    landParcel.setLocation(landParcelDetails.getLocation());
                    landParcel.setArea(landParcelDetails.getArea());
                    landParcel.setSurveyNumber(landParcelDetails.getSurveyNumber());
                    landParcel.setProject(landParcelDetails.getProject());
                    return ResponseEntity.ok(landParcelService.updateLandParcel(landParcel));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLandParcel(@PathVariable Long id) {
        return landParcelService.getLandParcelById(id)
                .map(landParcel -> {
                    landParcelService.deleteLandParcel(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}