package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.Notification;
import com.sih.landacquisitionsystem.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Notification> getNotificationsByRecipient(Long userId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getRecipient().getId().equals(userId))
                .toList();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Notification> getNotificationsByAcquisitionCase(Long caseId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getAcquisitionCase().getId().equals(caseId))
                .toList();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Notification updateNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}