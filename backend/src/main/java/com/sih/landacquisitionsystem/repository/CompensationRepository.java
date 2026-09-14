package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.Compensation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompensationRepository extends JpaRepository<Compensation, Long> {
    // We can add custom queries if needed
}