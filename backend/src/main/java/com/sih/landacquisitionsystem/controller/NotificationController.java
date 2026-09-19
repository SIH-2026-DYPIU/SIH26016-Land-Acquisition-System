package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.model.Notification;
import com.sih.landacquisitionsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(@PathVariable Long id, @RequestBody Notification notificationDetails) {
        return notificationService.getNotificationById(id)
                .map(notification -> {
                    notification.setTitle(notificationDetails.getTitle());
                    notification.setMessage(notificationDetails.getMessage());
                    notification.setNotificationType(notificationDetails.getNotificationType());
                    notification.setRead(notificationDetails.isRead());
                    notification.setRecipient(notificationDetails.getRecipient());
                    notification.setSender(notificationDetails.getSender());
                    notification.setAcquisitionCase(notificationDetails.getAcquisitionCase());
                    return ResponseEntity.ok(notificationService.updateNotification(notification));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(notification -> {
                    notificationService.deleteNotification(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}