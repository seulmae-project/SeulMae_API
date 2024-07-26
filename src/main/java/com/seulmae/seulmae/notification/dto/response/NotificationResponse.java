package com.seulmae.seulmae.notification.dto.response;

import com.seulmae.seulmae.notification.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private Long notificationId;
    private String title;
    private String message;
    private String notificationType;
    private LocalDateTime regDateNotification;

    public NotificationResponse(Notification n) {
        this.notificationId = n.getIdNotification();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.notificationType = n.getNotificationType().name();
        this.regDateNotification = n.getRegDateNotification();
    }
}
