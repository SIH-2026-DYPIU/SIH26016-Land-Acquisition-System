package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.AffectedFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffectedFamilyRepository extends JpaRepository<AffectedFamily, Long> {
}