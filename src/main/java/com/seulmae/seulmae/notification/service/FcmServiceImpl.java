package com.seulmae.seulmae.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.seulmae.seulmae.notification.dto.request.FcmSendRequest;
import com.seulmae.seulmae.notification.dto.response.FcmMessageResponse;
import com.seulmae.seulmae.notification.repository.FcmTokenRepository;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
    private final FcmTokenRepository fcmTokenRepository;


    @Value("${spring.firebase.privateKey}")
    private String PRIVATE_KEY;

    // 메세지를 구성하고 토큰을 받아서 FCM으로 메세지 처리를 수행하는 비즈니스 로직
    @Override
    public void sendMessageTo(String fcmToken, String title, String body) throws IOException {
        String message = makeMessage(fcmToken, title, body);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity entity = new HttpEntity<>(message, headers);

        String API_URL = "<https://fcm.googleapis.com/v1/projects/seulmae/messages:send>";
        ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        System.out.println("response = " + response);

        if (response.getStatusCode().isError()) {
            log.error("firebase 접속 에러 = {}", response.getBody());
        }


    }

    private String makeMessage(String fcmToken, String title, String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
//        User receiver = fcmTokenRepository.findUserByFcmToken(fcmToken)
//                .orElseThrow(() -> new NoSuchElementException("There is no user's FcmToken on Server"));
//        User receiver = null; // workplace이면서, manager인 사람, 끌고 오기.

        FcmMessageResponse fcmMessageResponse = FcmMessageResponse.builder()
                .message(FcmMessageResponse.Message.builder()
                        .fcmToken(fcmToken)
                        .notification(FcmMessageResponse.Notification.builder()
//                                .receiverId(receiver.getIdUser())
//                                .senderId(sender.getIdUser())
//                                .senderName(sender.getName())
//                                .senderProfileImageURL(sender.getUserImage())
                                .title(title)
                                .body(body)
                                .build())
                        .build())
                .validateOnly(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessageResponse);
    }


    private String getAccessToken() throws IOException {
        final String firebaseConfigPath = PRIVATE_KEY;

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }


    // Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰 발급

    // FCM 전송 정보를 기반으로 메세지 구성
}
