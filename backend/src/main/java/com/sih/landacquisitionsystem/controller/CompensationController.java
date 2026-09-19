package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.model.Compensation;
import com.sih.landacquisitionsystem.service.CompensationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compensations")
@RequiredArgsConstructor
public class CompensationController {

    private final CompensationService compensationService;

    @GetMapping
    public List<Compensation> getAllCompensations() {
        return compensationService.getAllCompensations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compensation> getCompensationById(@PathVariable Long id) {
        return compensationService.getCompensationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Compensation createCompensation(@RequestBody Compensation compensation) {
        return compensationService.createCompensation(compensation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compensation> updateCompensation(@PathVariable Long id, @RequestBody Compensation compensationDetails) {
        return compensationService.getCompensationById(id)
                .map(compensation -> {
                    compensation.setAmount(compensationDetails.getAmount());
                    compensation.setCurrency(compensationDetails.getCurrency());
                    compensation.setCompensationType(compensationDetails.getCompensationType());
                    compensation.setPaymentStatus(compensationDetails.getPaymentStatus());
                    compensation.setPaymentDate(compensationDetails.getPaymentDate());
                    compensation.setAcquisitionCase(compensationDetails.getAcquisitionCase());
                    return ResponseEntity.ok(compensationService.updateCompensation(compensation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompensation(@PathVariable Long id) {
        return compensationService.getCompensationById(id)
                .map(compensation -> {
                    compensationService.deleteCompensation(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}