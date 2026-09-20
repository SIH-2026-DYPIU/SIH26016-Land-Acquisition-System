package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.AcquisitionCase;
import com.sih.landacquisitionsystem.model.Notification;
import com.sih.landacquisitionsystem.model.StatusHistory;
import com.sih.landacquisitionsystem.model.User;
import com.sih.landacquisitionsystem.repository.AcquisitionCaseRepository;
import com.sih.landacquisitionsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AcquisitionCaseService {

    private final AcquisitionCaseRepository acquisitionCaseRepository;
    private final UserRepository userRepository;
    private final StatusHistoryService statusHistoryService;
    private final NotificationService notificationService;

    // Define valid status transitions
    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            "DRAFT", "SUBMITTED", "UNDER_REVIEW", "APPROVED", "REJECTED", "COMPENSATED", "CLOSED"
    ));

    private static final Set<String> VALID_TRANSITIONS_FROM_DRAFT = new HashSet<>(Arrays.asList("SUBMITTED"));
    private static final Set<String> VALID_TRANSITIONS_FROM_SUBMITTED = new HashSet<>(Arrays.asList("UNDER_REVIEW"));
    private static final Set<String> VALID_TRANSITIONS_FROM_UNDER_REVIEW = new HashSet<>(Arrays.asList("APPROVED", "REJECTED"));
    private static final Set<String> VALID_TRANSITIONS_FROM_APPROVED = new HashSet<>(Arrays.asList("COMPENSATED"));
    private static final Set<String> VALID_TRANSITIONS_FROM_REJECTED = new HashSet<>(Arrays.asList("DRAFT")); // allow resubmission
    private static final Set<String> VALID_TRANSITIONS_FROM_COMPENSATED = new HashSet<>(Arrays.asList("CLOSED"));
    private static final Set<String> VALID_TRANSITIONS_FROM_CLOSED = new HashSet<>(); // no further transitions

    public AcquisitionCase createAcquisitionCase(AcquisitionCase acquisitionCase) {
        // Set initial status to DRAFT if not set
        if (acquisitionCase.getStatus() == null) {
            acquisitionCase.setStatus("DRAFT");
        }
        AcquisitionCase savedCase = acquisitionCaseRepository.save(acquisitionCase);

        // Create a notification for the handler user (if any) when a case is created
        if (acquisitionCase.getHandlerUser() != null) {
            Notification notification = Notification.builder()
                    .title("New Acquisition Case Created")
                    .message("A new acquisition case has been created: " + savedCase.getCaseNumber())
                    .notificationType("CASE_CREATED")
                    .recipient(acquisitionCase.getHandlerUser())
                    .acquisitionCase(savedCase)
                    .build();
            notificationService.createNotification(notification);
        }

        return savedCase;
    }

    public Optional<AcquisitionCase> getAcquisitionCaseById(Long id) {
        return acquisitionCaseRepository.findById(id);
    }

    public Optional<AcquisitionCase> getAcquisitionCaseByCaseNumber(String caseNumber) {
        return acquisitionCaseRepository.findByCaseNumber(caseNumber);
    }

    public List<AcquisitionCase> getAllAcquisitionCases() {
        return acquisitionCaseRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public AcquisitionCase updateAcquisitionCase(AcquisitionCase acquisitionCase) {
        return acquisitionCaseRepository.save(acquisitionCase);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public void deleteAcquisitionCase(Long id) {
        acquisitionCaseRepository.deleteById(id);
    }

    /**
     * Update the status of an acquisition case with validation and create a status history entry.
     *
     * @param caseId         The ID of the acquisition case
     * @param newStatus      The new status to transition to
     * @param changedByUid   The Firebase UID of the user making the change
     * @param comments       Optional comments about the status change
     * @return The updated acquisition case
     */
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public AcquisitionCase updateStatus(Long caseId, String newStatus, String changedByUid, String comments) {
        AcquisitionCase acquisitionCase = acquisitionCaseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Acquisition case not found with id: " + caseId));

        String currentStatus = acquisitionCase.getStatus();

        // Validate new status
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        // Validate transition based on current status
        boolean validTransition = switch (currentStatus) {
            case "DRAFT" -> VALID_TRANSITIONS_FROM_DRAFT.contains(newStatus);
            case "SUBMITTED" -> VALID_TRANSITIONS_FROM_SUBMITTED.contains(newStatus);
            case "UNDER_REVIEW" -> VALID_TRANSITIONS_FROM_UNDER_REVIEW.contains(newStatus);
            case "APPROVED" -> VALID_TRANSITIONS_FROM_APPROVED.contains(newStatus);
            case "REJECTED" -> VALID_TRANSITIONS_FROM_REJECTED.contains(newStatus);
            case "COMPENSATED" -> VALID_TRANSITIONS_FROM_COMPENSATED.contains(newStatus);
            case "CLOSED" -> VALID_TRANSITIONS_FROM_CLOSED.contains(newStatus);
            default -> false;
        };

        if (!validTransition) {
            throw new IllegalArgumentException("Invalid transition from " + currentStatus + " to " + newStatus);
        }

        // Update the status
        acquisitionCase.setStatus(newStatus);

        // Save the updated case
        AcquisitionCase updatedCase = acquisitionCaseRepository.save(acquisitionCase);

        // Create a status history entry
        User changedByUser = userRepository.findByFirebaseUid(changedByUid)
                .orElseThrow(() -> new IllegalArgumentException("User not found with firebaseUid: " + changedByUid));

        StatusHistory statusHistory = StatusHistory.builder()
                .status(newStatus)
                .changedBy(changedByUser)
                .changedAt(java.time.Instant.now())
                .comments(comments)
                .acquisitionCase(updatedCase)
                .build();

        statusHistoryService.createStatusHistory(statusHistory);

        // Create a notification for the user who made the status change
        Notification notification = Notification.builder()
                .title("Acquisition Case Status Updated")
                .message("The status of case " + updatedCase.getCaseNumber() + " has been changed to " + newStatus)
                .notificationType("STATUS_UPDATED")
                .recipient(changedByUser)
                .acquisitionCase(updatedCase)
                .build();
        notificationService.createNotification(notification);

        return updatedCase;
    }
}