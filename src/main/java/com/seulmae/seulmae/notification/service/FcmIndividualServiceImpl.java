package com.seulmae.seulmae.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.dto.response.FcmMessageResponse;
import com.seulmae.seulmae.notification.repository.FcmTokenRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmIndividualServiceImpl implements FcmService {

    // 메세지를 구성하고 토큰을 받아서 FCM으로 메세지 처리를 수행하는 비즈니스 로직
    // 특정 기기에 메세지 전송
    @Override
    public void sendMessageTo(String fcmToken, String title, String body, NotificationType type, Long id) {
        Notification notification = FcmService.buildNotification(title, body);

        Message message = Message.builder()
                .putData("type", type.name())
                .putData("id", String.valueOf(id))
                .setToken(fcmToken)
                .setNotification(notification)
                .build();

        System.out.println("type = " + type.name());

        String response;
        try {
            response = FirebaseMessaging.getInstance().send(message);
            System.out.println("message = " + message);
            log.info("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase Message", e);
        }
    }

    public void sendMultiMessageTo(List<String> fcmTokens, String title, String body, NotificationType type, Long id, Long workplaceId) throws FirebaseMessagingException {
        Notification notification = FcmService.buildNotification(title, body);

        MulticastMessage message = MulticastMessage.builder()
                .putData("type", type.name())
                .putData("id", String.valueOf(id))
                .putData("workplaceId", String.valueOf(workplaceId))
                .setNotification(notification)
                .addAllTokens(fcmTokens)
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        // See the BatchResponse reference documentation
        // for the contents of response.
        System.out.println(response.getSuccessCount() + " messages were sent successfully");
    }



//    @Override
//    public void sendMessageTo(String fcmToken, String title, String body, NotificationType type, Long id) throws IOException {
//        String message = makeMessage(fcmToken, title, body);
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters()
//                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + getAccessToken());
//
//        HttpEntity entity = new HttpEntity<>(message, headers);
//
//        String API_URL = "https://fcm.googleapis.com/v1/projects/seulmae/messages:send";
//        ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
//
//        if (response.getStatusCode().isError()) {
//            log.error("firebase 접속 에러 = {}", response.getBody());
//        }
//
//    }
//
//
//    private String makeMessage(String fcmToken, String title, String body) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        FcmMessageResponse fcmMessageResponse = FcmMessageResponse.builder()
//                .message(FcmMessageResponse.Message.builder()
//                        .token(fcmToken)
//                        .notification(FcmMessageResponse.Notification.builder()
//                                .title(title)
//                                .body(body)
//                                .build())
//                        .build())
//                .validateOnly(false)
//                .build();
//        return objectMapper.writeValueAsString(fcmMessageResponse);
//    }

//    private String getAccessToken() throws IOException {
//        final String firebaseConfigPath = PRIVATE_KEY;
//
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//        googleCredentials.refreshIfExpired();
//        return googleCredentials.getAccessToken().getTokenValue();
//    }



}
