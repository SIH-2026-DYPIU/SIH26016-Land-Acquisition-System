package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.ProjectDTO;
import com.sih.landacquisitionsystem.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY')")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO, @RequestParam Long createdBy) {
        ProjectDTO createdProject = projectService.createProject(projectDTO, createdBy);
        return ResponseEntity.ok(createdProject);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO projectDTO = projectService.getProjectById(id);
        return ResponseEntity.ok(projectDTO);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/state/{state}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDTO>> getProjectsByState(@PathVariable String state) {
        List<ProjectDTO> projects = projectService.getProjectsByState(state);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/district/{district}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDTO>> getProjectsByDistrict(@PathVariable String district) {
        List<ProjectDTO> projects = projectService.getProjectsByDistrict(district);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDTO>> getProjectsByStatus(@PathVariable String status) {
        List<ProjectDTO> projects = projectService.getProjectsByStatus(status);
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CENTRAL_MINISTRY') or hasRole('STATE_AUTHORITY')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}