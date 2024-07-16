package com.seulmae.seulmae.notification.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.notification.entity.Notification;
import com.seulmae.seulmae.notification.repository.FcmTokenRepository;
import com.seulmae.seulmae.notification.repository.NotificationRepository;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final WorkplaceRepository workplaceRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

    private final FcmIndividualServiceImpl fcmIndividualServiceImpl;
    private final FcmTopicServiceImpl fcmTopicServiceImpl;

    private static final String TOPIC_PREFIX = "workplace";

    /**
     * 공지사항 알림 전송
     */
    @Transactional
    public void sendMessageToUsersAboutAnnouncement(Announcement announcement) {
        Workplace workplace = announcement.getWorkplace();
        List<User> users = userWorkplaceRepository.findUsersByWorkplace(workplace);
        String topic = workplace.getWorkplaceTopic();

        if (topic == null) {
            topic = TOPIC_PREFIX + UUID.randomUUID().toString().replace("-","");
            workplace.setWorkplaceTopic(topic);
            workplaceRepository.save(workplace);
        }

        String title = "[공지사항]";
        String body = "'" + announcement.getTitle() + "'이 등록되었습니다.";

        log.info("Sending announcement notification to topic: {}", topic);

        fcmTopicServiceImpl.sendMessageTo(topic, title, body, NotificationType.NOTICE, announcement.getIdAnnouncement());

        for (User user : users) {
            storeNotification(title, body, user, NotificationType.NOTICE);
        }
    }


    /**
     * 일대일(여러 기기) 메세지 전송
     */
    @Transactional
    public void sendMessageToUserWithMultiDevice(String title, String body, User receiver, NotificationType type, Long id) {
        try {

            System.out.println("receiver = " + receiver.getFcmTokens());
            List<String> fcmTokens = receiver.getFcmTokens().stream()
                    .map(FcmToken::getFcmToken)
                    .toList();

            fcmIndividualServiceImpl.sendMultiMessageTo(fcmTokens, title, body, type, id);
            storeNotification(title, body, receiver, type);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message to user {} with title '{}'", receiver.getUsername(), title, e);

            throw new RuntimeException("Failed to send FCM message", e);
        } catch (Exception e) {
            log.error("An error occurred while sending message to user {} with title '{}'", receiver.getUsername(), title, e);
            throw new RuntimeException("Failed to send message", e);
        }
    }



    public Notification storeNotification(String title, String message, User user, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .toUser(user)
                .notificationType(notificationType)
                .build();
        return notificationRepository.save(notification);
    }

}
