package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.AcquisitionStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcquisitionStageRepository extends JpaRepository<AcquisitionStage, Long> {
    // We can find by projectId if needed
    AcquisitionStage findByProjectId(Long projectId);
}