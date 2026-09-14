package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.dto.AffectedFamilyDTO;
import com.sih.landacquisitionsystem.model.AffectedFamily;
import com.sih.landacquisitionsystem.model.LandParcel;
import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.repository.AffectedFamilyRepository;
import com.sih.landacquisitionsystem.repository.LandParcelRepository;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffectedFamilyService {

    @Autowired
    private AffectedFamilyRepository affectedFamilyRepository;

    @Autowired
    private LandParcelRepository landParcelRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public AffectedFamilyDTO createAffectedFamily(AffectedFamilyDTO affectedFamilyDTO) {
        LandParcel parcel = landParcelRepository.findById(affectedFamilyDTO.getParcelId())
                .orElseThrow(() -> new RuntimeException("Land parcel not found"));
        Project project = projectRepository.findById(affectedFamilyDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        AffectedFamily affectedFamily = new AffectedFamily();
        affectedFamily.setFamilyIdentifier(affectedFamilyDTO.getFamilyIdentifier());
        affectedFamily.setParcel(parcel);
        affectedFamily.setProject(project);
        affectedFamily.setNumberOfMembers(affectedFamilyDTO.getNumberOfMembers());
        affectedFamily.setDisplacedStatus(affectedFamilyDTO.getDisplacedStatus());
        affectedFamily.setEligibility(affectedFamilyDTO.getEligibility());
        affectedFamily.setRrStatus(affectedFamilyDTO.getRrStatus());
        affectedFamily.setCreatedAt(LocalDateTime.now());

        AffectedFamily savedAffectedFamily = affectedFamilyRepository.save(affectedFamily);
        return convertToDTO(savedAffectedFamily);
    }

    public AffectedFamilyDTO getAffectedFamilyById(Long id) {
        AffectedFamily affectedFamily = affectedFamilyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affected family not found"));
        return convertToDTO(affectedFamily);
    }

    public List<AffectedFamilyDTO> getAllAffectedFamilies() {
        return affectedFamilyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AffectedFamilyDTO> getAffectedFamiliesByParcelId(Long parcelId) {
        return affectedFamilyRepository.findAll().stream()
                .filter(af -> af.getParcel().getId().equals(parcelId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AffectedFamilyDTO> getAffectedFamiliesByProjectId(Long projectId) {
        return affectedFamilyRepository.findAll().stream()
                .filter(af -> af.getProject().getId().equals(projectId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AffectedFamilyDTO updateAffectedFamily(Long id, AffectedFamilyDTO affectedFamilyDTO) {
        AffectedFamily affectedFamily = affectedFamilyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affected family not found"));

        LandParcel parcel = landParcelRepository.findById(affectedFamilyDTO.getParcelId())
                .orElseThrow(() -> new RuntimeException("Land parcel not found"));
        Project project = projectRepository.findById(affectedFamilyDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        affectedFamily.setFamilyIdentifier(affectedFamilyDTO.getFamilyIdentifier());
        affectedFamily.setParcel(parcel);
        affectedFamily.setProject(project);
        affectedFamily.setNumberOfMembers(affectedFamilyDTO.getNumberOfMembers());
        affectedFamily.setDisplacedStatus(affectedFamilyDTO.getDisplacedStatus());
        affectedFamily.setEligibility(affectedFamilyDTO.getEligibility());
        affectedFamily.setRrStatus(affectedFamilyDTO.getRrStatus());
        // createdAt remains unchanged

        AffectedFamily updatedAffectedFamily = affectedFamilyRepository.save(affectedFamily);
        return convertToDTO(updatedAffectedFamily);
    }

    public void deleteAffectedFamily(Long id) {
        if (!affectedFamilyRepository.existsById(id)) {
            throw new RuntimeException("Affected family not found");
        }
        affectedFamilyRepository.deleteById(id);
    }

    private AffectedFamilyDTO convertToDTO(AffectedFamily affectedFamily) {
        return AffectedFamilyDTO.builder()
                .id(affectedFamily.getId())
                .familyIdentifier(affectedFamily.getFamilyIdentifier())
                .parcelId(affectedFamily.getParcel().getId())
                .projectId(affectedFamily.getProject().getId())
                .numberOfMembers(affectedFamily.getNumberOfMembers())
                .displacedStatus(affectedFamily.getDisplacedStatus())
                .eligibility(affectedFamily.getEligibility())
                .rrStatus(affectedFamily.getRrStatus())
                .build();
    }
}