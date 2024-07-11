package com.seulmae.seulmae.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.user.entity.User;

import java.util.List;

public class FcmTopicService {
    // 구독 요청
    public String subscribeToTopic(User user, String topic) throws FirebaseMessagingException {
        // 등록 토큰 뽑아내기
        List<String> registrationTokens = user.getFcmTokens().stream()
                .map(FcmToken::getFcmToken).toList();
        //

        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
        System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");

        return null;
    }


    // 구독 취소
    public String unsubscribeToTopic(User user, String topic) throws FirebaseMessagingException {

        List<String> registrationTokens = user.getFcmTokens().stream()
                .map(FcmToken::getFcmToken).toList();
        TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
                registrationTokens, topic);

        System.out.println(response.getSuccessCount() + " tokens were unsubscribed successfully");

        return null;
    }

    // 주제로 메세지 전송

}
