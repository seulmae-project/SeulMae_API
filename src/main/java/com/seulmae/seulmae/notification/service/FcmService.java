package com.seulmae.seulmae.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.Notification;
import com.seulmae.seulmae.notification.NotificationType;

import java.io.IOException;

public interface FcmService {
    void sendMessageTo(String sendMethod, String title, String body, NotificationType type, Long id);

    static Notification buildNotification(String title, String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(null)
                .build();
    }
}
