package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.LandParcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandParcelRepository extends JpaRepository<LandParcel, Long> {
    // Find by project
    // We can add custom queries if needed
}