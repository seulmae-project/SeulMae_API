package com.seulmae.seulmae.notification.event;

import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.user.entity.User;
import lombok.Getter;

@Getter
public class MultiDeviceNotificationEvent implements NotificationEvent {
    private final String title;
    private final String body;
    private final User receiver;
    private final NotificationType type;
    private final Long id;
    private final Long workplaceId;

    public MultiDeviceNotificationEvent(String title, String body, User receiver, NotificationType type, Long id, Long workplaceId) {
        this.title = title;
        this.body = body;
        this.receiver = receiver;
        this.type = type;
        this.id = id;
        this.workplaceId = workplaceId;
    }

    @Override
    public NotificationType getType() {
        return type;
    }
}
