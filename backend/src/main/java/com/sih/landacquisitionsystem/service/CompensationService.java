package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.dto.CompensationDTO;
import com.sih.landacquisitionsystem.model.Compensation;
import com.sih.landacquisitionsystem.model.LandOwner;
import com.sih.landacquisitionsystem.model.LandParcel;
import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.repository.CompensationRepository;
import com.sih.landacquisitionsystem.repository.LandOwnerRepository;
import com.sih.landacquisitionsystem.repository.LandParcelRepository;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompensationService {

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private LandParcelRepository landParcelRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LandOwnerRepository landOwnerRepository;

    public CompensationDTO createCompensation(CompensationDTO compensationDTO) {
        LandParcel parcel = landParcelRepository.findById(compensationDTO.getParcelId())
                .orElseThrow(() -> new RuntimeException("Land parcel not found"));
        Project project = projectRepository.findById(compensationDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        LandOwner beneficiary = landOwnerRepository.findById(compensationDTO.getBeneficiaryId())
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));

        Compensation compensation = new Compensation();
        compensation.setParcel(parcel);
        compensation.setProject(project);
        compensation.setBeneficiary(beneficiary);
        compensation.setAssessedAmount(compensationDTO.getAssessedAmount());
        compensation.setApprovedAmount(compensationDTO.getApprovedAmount());
        compensation.setPaidAmount(compensationDTO.getPaidAmount());
        compensation.setPaidDate(compensationDTO.getPaidDate());
        compensation.setTransactionReference(compensationDTO.getTransactionReference());
        compensation.setStatus(compensationDTO.getStatus());

        Compensation savedCompensation = compensationRepository.save(compensation);
        return convertToDTO(savedCompensation);
    }

    public CompensationDTO getCompensationById(Long id) {
        Compensation compensation = compensationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compensation not found"));
        return convertToDTO(compensation);
    }

    public List<CompensationDTO> getAllCompensations() {
        return compensationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CompensationDTO> getCompensationsByParcelId(Long parcelId) {
        return compensationRepository.findAll().stream()
                .filter(comp -> comp.getParcel().getId().equals(parcelId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CompensationDTO updateCompensation(Long id, CompensationDTO compensationDTO) {
        Compensation compensation = compensationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compensation not found"));

        LandParcel parcel = landParcelRepository.findById(compensationDTO.getParcelId())
                .orElseThrow(() -> new RuntimeException("Land parcel not found"));
        Project project = projectRepository.findById(compensationDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        LandOwner beneficiary = landOwnerRepository.findById(compensationDTO.getBeneficiaryId())
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));

        compensation.setParcel(parcel);
        compensation.setProject(project);
        compensation.setBeneficiary(beneficiary);
        compensation.setAssessedAmount(compensationDTO.getAssessedAmount());
        compensation.setApprovedAmount(compensationDTO.getApprovedAmount());
        compensation.setPaidAmount(compensationDTO.getPaidAmount());
        compensation.setPaidDate(compensationDTO.getPaidDate());
        compensation.setTransactionReference(compensationDTO.getTransactionReference());
        compensation.setStatus(compensationDTO.getStatus());

        Compensation updatedCompensation = compensationRepository.save(compensation);
        return convertToDTO(updatedCompensation);
    }

    public void deleteCompensation(Long id) {
        if (!compensationRepository.existsById(id)) {
            throw new RuntimeException("Compensation not found");
        }
        compensationRepository.deleteById(id);
    }

    private CompensationDTO convertToDTO(Compensation compensation) {
        return CompensationDTO.builder()
                .id(compensation.getId())
                .parcelId(compensation.getParcel().getId())
                .projectId(compensation.getProject().getId())
                .beneficiaryId(compensation.getBeneficiary().getId())
                .assessedAmount(compensation.getAssessedAmount())
                .approvedAmount(compensation.getApprovedAmount())
                .paidAmount(compensation.getPaidAmount())
                .paidDate(compensation.getPaidDate())
                .transactionReference(compensation.getTransactionReference())
                .status(compensation.getStatus())
                .build();
    }
}