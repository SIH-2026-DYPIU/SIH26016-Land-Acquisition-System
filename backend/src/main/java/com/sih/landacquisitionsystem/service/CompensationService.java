package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.Compensation;
import com.sih.landacquisitionsystem.repository.CompensationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompensationService {

    private final CompensationRepository compensationRepository;

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Compensation createCompensation(Compensation compensation) {
        return compensationRepository.save(compensation);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Optional<Compensation> getCompensationById(Long id) {
        return compensationRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Compensation> getAllCompensations() {
        return compensationRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Optional<Compensation> getCompensationByAcquisitionCase(Long caseId) {
        return compensationRepository.findAll().stream()
                .filter(c -> c.getAcquisitionCase().getId().equals(caseId))
                .findFirst();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Compensation updateCompensation(Compensation compensation) {
        return compensationRepository.save(compensation);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public void deleteCompensation(Long id) {
        compensationRepository.deleteById(id);
    }
}