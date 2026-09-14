package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.dto.ProjectDTO;
import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.model.User;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import com.sih.landacquisitionsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public ProjectDTO createProject(ProjectDTO projectDTO, Long createdById) {
        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = new Project();
        project.setProjectCode(projectDTO.getProjectCode());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setProjectType(projectDTO.getProjectType());
        project.setMinistry(projectDTO.getMinistry());
        project.setImplementingAgency(projectDTO.getImplementingAgency());
        project.setAcquiringAuthority(projectDTO.getAcquiringAuthority());
        project.setState(projectDTO.getState());
        project.setDistrict(projectDTO.getDistrict());
        project.setVillage(projectDTO.getVillage());
        project.setProposedArea(projectDTO.getProposedArea());
        project.setAcquiredArea(projectDTO.getAcquiredArea());
        project.setStartDate(projectDTO.getStartDate());
        project.setExpectedCompletionDate(projectDTO.getExpectedCompletionDate());
        project.setStatus(projectDTO.getStatus());
        project.setCreatedBy(createdBy);
        project.setCreatedAt(LocalDateTime.now());
        // updatedAt remains null on creation

        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return convertToDTO(project);
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByState(String state) {
        return projectRepository.findByState(state).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByDistrict(String district) {
        return projectRepository.findByDistrict(district).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(Project.Status.valueOf(status)).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setProjectCode(projectDTO.getProjectCode());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setProjectType(projectDTO.getProjectType());
        project.setMinistry(projectDTO.getMinistry());
        project.setImplementingAgency(projectDTO.getImplementingAgency());
        project.setAcquiringAuthority(projectDTO.getAcquiringAuthority());
        project.setState(projectDTO.getState());
        project.setDistrict(projectDTO.getDistrict());
        project.setVillage(projectDTO.getVillage());
        project.setProposedArea(projectDTO.getProposedArea());
        project.setAcquiredArea(projectDTO.getAcquiredArea());
        project.setStartDate(projectDTO.getStartDate());
        project.setExpectedCompletionDate(projectDTO.getExpectedCompletionDate());
        project.setStatus(projectDTO.getStatus());
        // updatedBy? We don't have updatedBy in the model, but we can add if needed. For now, we'll just update the updatedAt timestamp.
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    private ProjectDTO convertToDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .projectCode(project.getProjectCode())
                .name(project.getName())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .ministry(project.getMinistry())
                .implementingAgency(project.getImplementingAgency())
                .acquiringAuthority(project.getAcquiringAuthority())
                .state(project.getState())
                .district(project.getDistrict())
                .village(project.getVillage())
                .proposedArea(project.getProposedArea())
                .acquiredArea(project.getAcquiredArea())
                .startDate(project.getStartDate())
                .expectedCompletionDate(project.getExpectedCompletionDate())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy().getId())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}