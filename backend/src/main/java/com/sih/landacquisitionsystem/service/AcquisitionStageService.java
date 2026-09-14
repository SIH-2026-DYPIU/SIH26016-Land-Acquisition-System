package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.dto.AcquisitionStageDTO;
import com.sih.landacquisitionsystem.model.AcquisitionStage;
import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.model.User;
import com.sih.landacquisitionsystem.model.Project.Status;
import com.sih.landacquisitionsystem.repository.AcquisitionStageRepository;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import com.sih.landacquisitionsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcquisitionStageService {

    @Autowired
    private AcquisitionStageRepository acquisitionStageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public AcquisitionStageDTO createStage(AcquisitionStageDTO stageDTO) {
        Project project = projectRepository.findById(stageDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User updatedBy = userRepository.findById(stageDTO.getUpdatedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AcquisitionStage stage = new AcquisitionStage();
        stage.setProject(project);
        stage.setStage(stageDTO.getStage());
        stage.setUpdatedBy(updatedBy);
        stage.setUpdatedAt(LocalDateTime.now());
        stage.setRemarks(stageDTO.getRemarks());

        AcquisitionStage savedStage = acquisitionStageRepository.save(stage);
        return convertToDTO(savedStage);
    }

    public AcquisitionStageDTO getStageById(Long id) {
        AcquisitionStage stage = acquisitionStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acquisition stage not found"));
        return convertToDTO(stage);
    }

    public AcquisitionStageDTO getStageByProjectId(Long projectId) {
        AcquisitionStage stage = acquisitionStageRepository.findByProjectId(projectId);
        if (stage == null) {
            throw new RuntimeException("Acquisition stage not found for project");
        }
        return convertToDTO(stage);
    }

    public List<AcquisitionStageDTO> getAllStages() {
        return acquisitionStageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AcquisitionStageDTO updateStage(Long id, AcquisitionStageDTO stageDTO) {
        AcquisitionStage stage = acquisitionStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acquisition stage not found"));

        Project project = projectRepository.findById(stageDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User updatedBy = userRepository.findById(stageDTO.getUpdatedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        stage.setProject(project);
        stage.setStage(stageDTO.getStage());
        stage.setUpdatedBy(updatedBy);
        stage.setUpdatedAt(LocalDateTime.now());
        stage.setRemarks(stageDTO.getRemarks());

        AcquisitionStage updatedStage = acquisitionStageRepository.save(stage);
        return convertToDTO(updatedStage);
    }

    public void deleteStage(Long id) {
        if (!acquisitionStageRepository.existsById(id)) {
            throw new RuntimeException("Acquisition stage not found");
        }
        acquisitionStageRepository.deleteById(id);
    }

    // Method to advance stage to the next sequential stage
    public AcquisitionStageDTO advanceStage(Long projectId, Long updatedById, String remarks) {
        // Get current stage for the project
        AcquisitionStage currentStage = acquisitionStageRepository.findByProjectId(projectId);
        if (currentStage == null) {
            throw new RuntimeException("No acquisition stage found for project");
        }

        // Define the sequence of stages based on Project.Status
        Status[] stageSequence = {
                Status.DRAFT,
                Status.SUBMITTED,
                Status.UNDER_SCRUTINY,
                Status.APPROVED,
                Status.LAND_IDENTIFICATION,
                Status.NOTIFICATION,
                Status.AWARD,
                Status.COMPENSATION,
                Status.POSSESSION,
                Status.R_AND_R,
                Status.COMPLETED
        };

        // Find current stage index
        int currentIndex = -1;
        for (int i = 0; i < stageSequence.length; i++) {
            if (stageSequence[i] == currentStage.getStage()) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            throw new RuntimeException("Invalid current stage");
        }

        // Check if we can advance (not at the final stage)
        if (currentIndex >= stageSequence.length - 1) {
            throw new RuntimeException("Cannot advance further; already at final stage");
        }

        // Get next stage
        Status nextStage = stageSequence[currentIndex + 1];

        // Update the stage
        currentStage.setStage(nextStage);
        currentStage.setUpdatedBy(userRepository.findById(updatedById)
                .orElseThrow(() -> new RuntimeException("User not found")));
        currentStage.setUpdatedAt(LocalDateTime.now());
        currentStage.setRemarks(remarks);

        AcquisitionStage updatedStage = acquisitionStageRepository.save(currentStage);
        return convertToDTO(updatedStage);
    }

    private AcquisitionStageDTO convertToDTO(AcquisitionStage stage) {
        return AcquisitionStageDTO.builder()
                .id(stage.getId())
                .projectId(stage.getProject().getId())
                .stage(stage.getStage())
                .updatedBy(stage.getUpdatedBy().getId())
                .remarks(stage.getRemarks())
                .build();
    }
}