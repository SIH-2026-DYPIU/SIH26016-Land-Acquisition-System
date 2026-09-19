package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Find by acquisition case
}