package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.model.StatusHistory;
import com.sih.landacquisitionsystem.service.StatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status-history")
@RequiredArgsConstructor
public class StatusHistoryController {

    private final StatusHistoryService statusHistoryService;

    @GetMapping
    public List<StatusHistory> getAllStatusHistory() {
        return statusHistoryService.getAllStatusHistory();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusHistory> getStatusHistoryById(@PathVariable Long id) {
        return statusHistoryService.getStatusHistoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StatusHistory createStatusHistory(@RequestBody StatusHistory statusHistory) {
        return statusHistoryService.createStatusHistory(statusHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusHistory> updateStatusHistory(@PathVariable Long id, @RequestBody StatusHistory statusHistoryDetails) {
        return statusHistoryService.getStatusHistoryById(id)
                .map(statusHistory -> {
                    statusHistory.setStatus(statusHistoryDetails.getStatus());
                    statusHistory.setChangedBy(statusHistoryDetails.getChangedBy());
                    statusHistory.setChangedAt(statusHistoryDetails.getChangedAt());
                    statusHistory.setComments(statusHistoryDetails.getComments());
                    statusHistory.setAcquisitionCase(statusHistoryDetails.getAcquisitionCase());
                    return ResponseEntity.ok(statusHistoryService.updateStatusHistory(statusHistory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatusHistory(@PathVariable Long id) {
        return statusHistoryService.getStatusHistoryById(id)
                .map(statusHistory -> {
                    statusHistoryService.deleteStatusHistory(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}