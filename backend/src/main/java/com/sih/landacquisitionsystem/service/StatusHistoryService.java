package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.StatusHistory;
import com.sih.landacquisitionsystem.repository.StatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public StatusHistory createStatusHistory(StatusHistory statusHistory) {
        return statusHistoryRepository.save(statusHistory);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Optional<StatusHistory> getStatusHistoryById(Long id) {
        return statusHistoryRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<StatusHistory> getAllStatusHistory() {
        return statusHistoryRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<StatusHistory> getStatusHistoryByAcquisitionCase(Long caseId) {
        return statusHistoryRepository.findAll().stream()
                .filter(sh -> sh.getAcquisitionCase().getId().equals(caseId))
                .toList();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public StatusHistory updateStatusHistory(StatusHistory statusHistory) {
        return statusHistoryRepository.save(statusHistory);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public void deleteStatusHistory(Long id) {
        statusHistoryRepository.deleteById(id);
    }
}