package com.sih.landacquisitionsystem.repository;

import com.sih.landacquisitionsystem.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find by recipient user
    // Find by acquisition case
}