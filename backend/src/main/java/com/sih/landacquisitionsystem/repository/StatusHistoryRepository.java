package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    // Find by acquisition case, order by changedAt desc
}