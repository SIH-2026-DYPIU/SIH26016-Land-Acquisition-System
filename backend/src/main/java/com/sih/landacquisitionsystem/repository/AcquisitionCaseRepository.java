package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.AcquisitionCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AcquisitionCaseRepository extends JpaRepository<AcquisitionCase, Long> {
    Optional<AcquisitionCase> findByCaseNumber(String caseNumber);
    // Find by land parcel
    // Find by status
    // Find by handler user
}