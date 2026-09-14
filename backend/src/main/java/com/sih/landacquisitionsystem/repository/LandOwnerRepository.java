package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.LandOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandOwnerRepository extends JpaRepository<LandOwner, Long> {
}