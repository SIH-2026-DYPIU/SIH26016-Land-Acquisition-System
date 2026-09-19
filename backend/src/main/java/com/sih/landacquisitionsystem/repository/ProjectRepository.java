package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Find by department
    // We can add custom queries if needed
}