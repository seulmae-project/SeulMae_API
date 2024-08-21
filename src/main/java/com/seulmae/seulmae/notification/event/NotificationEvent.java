package com.seulmae.seulmae.notification.event;

import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.user.entity.User;
import lombok.Getter;


public interface NotificationEvent {
    NotificationType getType();
}
