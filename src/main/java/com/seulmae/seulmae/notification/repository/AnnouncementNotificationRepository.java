package com.seulmae.seulmae.notification.repository;

import com.seulmae.seulmae.notification.entity.AnnouncementNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementNotificationRepository extends JpaRepository<AnnouncementNotification, Long> {
}
