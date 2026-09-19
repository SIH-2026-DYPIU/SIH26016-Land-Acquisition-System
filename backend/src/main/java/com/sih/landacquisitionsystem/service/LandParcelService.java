package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.LandParcel;
import com.sih.landacquisitionsystem.repository.LandParcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LandParcelService {

    private final LandParcelRepository landParcelRepository;

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public LandParcel createLandParcel(LandParcel landParcel) {
        return landParcelRepository.save(landParcel);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Optional<LandParcel> getLandParcelById(Long id) {
        return landParcelRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<LandParcel> getAllLandParcels() {
        return landParcelRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<LandParcel> getLandParcelsByProject(Long projectId) {
        return landParcelRepository.findAll().stream()
                .filter(p -> p.getProject().getId().equals(projectId))
                .toList();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public LandParcel updateLandParcel(LandParcel landParcel) {
        return landParcelRepository.save(landParcel);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public void deleteLandParcel(Long id) {
        landParcelRepository.deleteById(id);
    }
}