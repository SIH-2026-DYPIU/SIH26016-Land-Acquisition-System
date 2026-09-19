package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Project> getProjectsByDepartment(Long departmentId) {
        return projectRepository.findAll().stream()
                .filter(p -> p.getDepartment().getId().equals(departmentId))
                .toList();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}