package com.seulmae.seulmae.notification.event;

import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.notification.NotificationType;
import lombok.Getter;

@Getter
public class AnnouncementNotificationEvent implements NotificationEvent {
    private final Announcement announcement;

    public AnnouncementNotificationEvent(Announcement announcement) {
        this.announcement = announcement;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.NOTICE;
    }
}
