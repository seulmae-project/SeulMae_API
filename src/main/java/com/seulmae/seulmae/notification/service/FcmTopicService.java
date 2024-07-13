package com.seulmae.seulmae.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FcmTopicService {
    // 구독 요청
    public void subscribeToTopic(User user, String topic) throws FirebaseMessagingException {
        // 등록 토큰 뽑아내기
        List<String> registrationTokens = user.getFcmTokens().stream()
                .map(FcmToken::getFcmToken).toList();

        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
        log.info(response.getSuccessCount() + " tokens were subscribed successfully");

    }


    // 구독 취소
    public void unsubscribeToTopic(User user, String topic) throws FirebaseMessagingException {

        List<String> registrationTokens = user.getFcmTokens().stream()
                .map(FcmToken::getFcmToken).toList();
        TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
                registrationTokens, topic);

        log.info(response.getSuccessCount() + " tokens were unsubscribed successfully");
    }

    // 주제로 메세지 전송

}
