package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByState(String state);
    List<Project> findByDistrict(String district);
    List<Project> findByStatus(com.sih.landacquisitionsystem.model.Project.Status status);
    List<Project> findByStateAndDistrict(String state, String district);

    @Query("SELECT p FROM Project p WHERE p.state = :state AND p.status = :status")
    List<Project> findByStateAndStatus(@Param("state") String state, @Param("status") com.sih.landacquisitionsystem.model.Project.Status status);
}