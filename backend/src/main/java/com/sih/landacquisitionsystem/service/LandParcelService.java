package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.dto.LandParcelDTO;
import com.sih.landacquisitionsystem.model.LandParcel;
import com.sih.landacquisitionsystem.model.LandOwner;
import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.repository.LandOwnerRepository;
import com.sih.landacquisitionsystem.repository.LandParcelRepository;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LandParcelService {

    @Autowired
    private LandParcelRepository landParcelRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LandOwnerRepository landOwnerRepository;

    public LandParcelDTO createParcel(LandParcelDTO landParcelDTO) {
        Project project = projectRepository.findById(landParcelDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        LandParcel landParcel = new LandParcel();
        landParcel.setProject(project);
        landParcel.setParcelNumber(landParcelDTO.getParcelNumber());
        landParcel.setSurveyNumber(landParcelDTO.getSurveyNumber());
        landParcel.setState(landParcelDTO.getState());
        landParcel.setDistrict(landParcelDTO.getDistrict());
        landParcel.setVillage(landParcelDTO.getVillage());
        landParcel.setLandType(landParcelDTO.getLandType());
        landParcel.setArea(landParcelDTO.getArea());
        landParcel.setLatitude(landParcelDTO.getLatitude());
        landParcel.setLongitude(landParcelDTO.getLongitude());
        // Set landOwner if landOwnerId is provided
        if (landParcelDTO.getLandOwnerId() != null) {
            LandOwner landOwner = landOwnerRepository.findById(landParcelDTO.getLandOwnerId())
                    .orElseThrow(() -> new RuntimeException("Land owner not found"));
            landParcel.setLandOwner(landOwner);
        }
        landParcel.setStatus(landParcelDTO.getStatus());

        LandParcel savedParcel = landParcelRepository.save(landParcel);
        return convertToDTO(savedParcel);
    }

    public LandParcelDTO getParcelById(Long id) {
        LandParcel landParcel = landParcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land parcel not found"));
        return convertToDTO(landParcel);
    }

    public List<LandParcelDTO> getAllParcels() {
        return landParcelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LandParcelDTO> getParcelsByProjectId(Long projectId) {
        return landParcelRepository.findAll().stream()
                .filter(parcel -> parcel.getProject().getId().equals(projectId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LandParcelDTO updateParcel(Long id, LandParcelDTO landParcelDTO) {
        LandParcel landParcel = landParcelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land parcel not found"));

        Project project = projectRepository.findById(landParcelDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        landParcel.setProject(project);
        landParcel.setParcelNumber(landParcelDTO.getParcelNumber());
        landParcel.setSurveyNumber(landParcelDTO.getSurveyNumber());
        landParcel.setState(landParcelDTO.getState());
        landParcel.setDistrict(landParcelDTO.getDistrict());
        landParcel.setVillage(landParcelDTO.getVillage());
        landParcel.setLandType(landParcelDTO.getLandType());
        landParcel.setArea(landParcelDTO.getArea());
        landParcel.setLatitude(landParcelDTO.getLatitude());
        landParcel.setLongitude(landParcelDTO.getLongitude());
        // Set landOwner if landOwnerId is provided
        if (landParcelDTO.getLandOwnerId() != null) {
            LandOwner landOwner = landOwnerRepository.findById(landParcelDTO.getLandOwnerId())
                    .orElseThrow(() -> new RuntimeException("Land owner not found"));
            landParcel.setLandOwner(landOwner);
        } else {
            landParcel.setLandOwner(null);
        }
        landParcel.setStatus(landParcelDTO.getStatus());

        LandParcel updatedParcel = landParcelRepository.save(landParcel);
        return convertToDTO(updatedParcel);
    }

    public void deleteParcel(Long id) {
        if (!landParcelRepository.existsById(id)) {
            throw new RuntimeException("Land parcel not found");
        }
        landParcelRepository.deleteById(id);
    }

    private LandParcelDTO convertToDTO(LandParcel landParcel) {
        return LandParcelDTO.builder()
                .id(landParcel.getId())
                .parcelNumber(landParcel.getParcelNumber())
                .surveyNumber(landParcel.getSurveyNumber())
                .projectId(landParcel.getProject().getId())
                .state(landParcel.getState())
                .district(landParcel.getDistrict())
                .village(landParcel.getVillage())
                .landType(landParcel.getLandType())
                .area(landParcel.getArea())
                .latitude(landParcel.getLatitude())
                .longitude(landParcel.getLongitude())
                .landOwnerId(landParcel.getLandOwner() != null ? landParcel.getLandOwner().getId() : null)
                .status(landParcel.getStatus())
                .build();
    }
}