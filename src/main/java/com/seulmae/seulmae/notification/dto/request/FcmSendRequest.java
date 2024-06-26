package com.seulmae.seulmae.notification.dto.request;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendRequest {
    private String fcmToken; // 디바이스 기기에서 발급받은 FCM 토큰. // 해당 토큰으로 유저를 구별하자.
    private String title; // 메세지 제목
    private String body; // 메세지 내용

    @Builder(toBuilder = true)
    public FcmSendRequest(String fcmToken, String title, String body) {
        this.fcmToken = fcmToken;
        this.title = title;
        this.body = body;
    }
}
