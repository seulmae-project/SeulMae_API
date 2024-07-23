package com.seulmae.seulmae.notification.repository;

import com.seulmae.seulmae.notification.entity.Notification;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserWorkplace(UserWorkplace userWorkplace);
}
