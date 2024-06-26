package com.seulmae.seulmae.notification.service;

import com.seulmae.seulmae.notification.dto.request.FcmSendRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface FcmService {
    void sendMessageTo(String fcmToken, String title, String body) throws IOException;
}
