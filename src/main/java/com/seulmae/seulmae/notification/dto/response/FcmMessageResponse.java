package com.seulmae.seulmae.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FcmMessageResponse {

    @JsonProperty("validate_only")
    private boolean validateOnly; // 테스트용 여부 확인 변수 (true ->실제 알림요청을 보내지 않음)
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;
        private String token;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Notification {
//        private String senderName; // 보내는 사람
//        private Long senderId; // 보내는 사람 ID
//        private Long receiverId; // 받는 사람 ID
        private String title; // 알림 제목
        private String body; // 알림 본문
//        private String senderProfileImageURL; // 보내는 사람의 프로필 이미지 URL
    }
}
