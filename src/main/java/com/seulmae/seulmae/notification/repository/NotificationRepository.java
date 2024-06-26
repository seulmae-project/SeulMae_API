package com.seulmae.seulmae.notification.repository;

import com.seulmae.seulmae.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
