package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.AcquisitionCase;
import com.sih.landacquisitionsystem.model.StatusHistory;
import com.sih.landacquisitionsystem.model.User;
import com.sih.landacquisitionsystem.repository.AcquisitionCaseRepository;
import com.sih.landacquisitionsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcquisitionCaseServiceTest {

    @Mock
    private AcquisitionCaseRepository acquisitionCaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StatusHistoryService statusHistoryService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AcquisitionCaseService acquisitionCaseService;

    private AcquisitionCase acquisitionCase;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firebaseUid("test-uid")
                .email("test@example.com")
                .name("Test User")
                .role("OFFICER")
                .build();

        acquisitionCase = AcquisitionCase.builder()
                .id(1L)
                .caseNumber("CASE-001")
                .status("DRAFT")
                .claimantName("John Doe")
                .claimantContactInfo("123-456-7890")
                .handlerUser(user)
                .build();
    }

    @Test
    void testUpdateStatusSuccess() {
        // Arrange
        when(acquisitionCaseRepository.findById(1L)).thenReturn(Optional.of(acquisitionCase));
        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(user));
        when(acquisitionCaseRepository.save(any(AcquisitionCase.class))).thenReturn(acquisitionCase);
        when(statusHistoryService.createStatusHistory(any(StatusHistory.class))).thenReturn(new StatusHistory());

        // Act
        AcquisitionCase result = acquisitionCaseService.updateStatus(1L, "SUBMITTED", "test-uid", "Testing status update");

        // Assert
        assertNotNull(result);
        assertEquals("SUBMITTED", result.getStatus());
        verify(statusHistoryService, times(1)).createStatusHistory(any(StatusHistory.class));
        verify(notificationService, times(1)).createNotification(any());
    }

    @Test
    void testUpdateStatusInvalidTransition() {
        // Arrange
        when(acquisitionCaseRepository.findById(1L)).thenReturn(Optional.of(acquisitionCase));
        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            acquisitionCaseService.updateStatus(1L, "APPROVED", "test-uid", "Invalid transition");
        });
        assertTrue(exception.getMessage().contains("Invalid transition"));
    }
}