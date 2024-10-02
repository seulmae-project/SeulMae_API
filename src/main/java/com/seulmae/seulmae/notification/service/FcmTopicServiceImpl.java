package com.seulmae.seulmae.notification.service;

import com.google.firebase.messaging.*;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FcmTopicServiceImpl implements FcmService {

    @Override
    public void sendMessageTo(String topic, String title, String body, NotificationType type, Long id) {
        // 주제로 메세지 전송
        Notification notification = FcmService.buildNotification(topic, title);

        Message message = Message.builder()
                .putData("type", type.name())
                .putData("id", String.valueOf(id))
                .setTopic(topic)
                .setNotification(notification)
                .build();

        System.out.println("type = " + type.name());

        String response;

        try {
            response = FirebaseMessaging.getInstance().send(message);
            System.out.println("message = " + message);
            log.info("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase notification", e);
        }
    }

    // 구독 요청
    public void subscribeToTopic(User user, String topic) throws FirebaseMessagingException {

        if (user.getFcmTokens() != null && !user.getFcmTokens().isEmpty()) {
            // 등록 토큰 뽑아내기
            List<String> registrationTokens = user.getFcmTokens().stream()
                    .map(FcmToken::getFcmToken).toList();

            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
            log.info(response.getSuccessCount() + " tokens were subscribed successfully");
        }

    }


    // 구독 취소
    public void unsubscribeFromTopic(User user, String topic) throws FirebaseMessagingException {

        if (user.getFcmTokens() != null && !user.getFcmTokens().isEmpty()) {
            List<String> registrationTokens = user.getFcmTokens().stream()
                    .map(FcmToken::getFcmToken).toList();
            TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
                    registrationTokens, topic);
            log.info(response.getSuccessCount() + " tokens were unsubscribed successfully");
        }

    }

    // 구독 전체 취소
    public void unsubscribeAllFromTopic(List<User> users, String topic) throws FirebaseMessagingException {
        for (User user : users) {
            unsubscribeFromTopic(user, topic);
        }
    }
}
