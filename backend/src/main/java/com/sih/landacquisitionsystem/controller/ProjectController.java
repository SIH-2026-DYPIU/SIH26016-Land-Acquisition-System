package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.dto.ProjectDTO;
import com.sih.landacquisitionsystem.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> create(@RequestBody ProjectDTO dto, @RequestParam Long createdById) {
        return ResponseEntity.ok(projectService.createProject(dto, createdById));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAll() { return ResponseEntity.ok(projectService.getAllProjects()); }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) { return ResponseEntity.ok(projectService.getProjectById(id)); }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<ProjectDTO>> byState(@PathVariable String state) { return ResponseEntity.ok(projectService.getProjectsByState(state)); }

    @GetMapping("/district/{district}")
    public ResponseEntity<List<ProjectDTO>> byDistrict(@PathVariable String district) { return ResponseEntity.ok(projectService.getProjectsByDistrict(district)); }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectDTO>> byStatus(@PathVariable String status) { return ResponseEntity.ok(projectService.getProjectsByStatus(status)); }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) { return ResponseEntity.ok(projectService.updateProject(id, dto)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { projectService.deleteProject(id); return ResponseEntity.noContent().build(); }
}
