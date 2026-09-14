package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.LandParcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandParcelRepository extends JpaRepository<LandParcel, Long> {
    // We can add custom queries if needed, but for now, basic CRUD is enough
}