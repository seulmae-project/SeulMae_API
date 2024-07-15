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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
//    private final UserNotificationRepository userNotificationRepository;
//    private final AnnouncementNotificationRepository announcementNotificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final WorkplaceRepository workplaceRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

    private final FcmIndividualServiceImpl fcmIndividualServiceImpl;
    private final FcmTopicServiceImpl fcmTopicServiceImpl;

    private static final String TOPIC_PREFIX = "workplace";
    // 공지사항 - 해당 근무지 소속 전체 유저에게 발송
    @Transactional
    public void sendMessageToUsersAboutAnnouncement(Announcement announcement) throws IOException {
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

    @Transactional
    public void sendMessageToUserWithMultiDevice(String title, String body, User receiver, NotificationType type, Long id) {
        try {
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

//    @Transactional
//    public void sendMessageToManagerForAttendanceRequest(FcmSendRequest request, User sender) throws IOException {
//        // 해당 fcm 토큰이 존재하는 지 여부 확인하기
//        User receiver = fcmTokenRepository.findUserByFcmToken(request.getFcmToken())
//                .orElseThrow(() -> new NoSuchElementException("There is no user's FcmToken on Server"));
//
//        Long attendanceRequestHistory = null; // 어떻게 받아내...?
//
//        // 메세지 보내기
//        fcmIndividualServiceImpl.sendMessageTo(request.getFcmToken(), request.getTitle(), request.getBody());
//
//        // notification 저장하기
//        Notification notification = storeNotification(request.getTitle(), request.getBody(), receiver, NotificationType.ATTENDANCE_REQUEST);
//        // userNotification 저장하기
////        storeUserNotification(sender, receiver, attendanceRequestHistory, notification, false);
//    }
//
//    @Transactional
//    public void sendMessageToAlbaForAttendanceResponse(FcmSendRequest request, User sender) throws IOException {
//        User receiver = fcmTokenRepository.findUserByFcmToken(request.getFcmToken())
//                .orElseThrow(() -> new NoSuchElementException("There is no user's FcmToken on Server"));
//
//        Long attendanceRequestHistory = null; // 어떻게 받아내...?
//
//        // 메세지 보내기
//        fcmIndividualServiceImpl.sendMessageTo(request.getFcmToken(), request.getTitle(), request.getBody());
//
//        // notification 저장하기
//        Notification notification = storeNotification(request.getTitle(), request.getBody(), receiver, NotificationType.ATTENDANCE_RESPONSE);
//        // userNotification 저장하기
////        storeUserNotification(sender, receiver, attendanceRequestHistory, notification, false);
//    }




    public Notification storeNotification(String title, String message, User user, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .toUser(user)
                .notificationType(notificationType)
                .build();
        return notificationRepository.save(notification);
    }

//    private UserNotification storeUserNotification(User sender, User receiver, Long attendanceRequestHistory, Notification notification) {
//        UserNotification userNotification = UserNotification.builder()
//                .toUser(receiver)
//                .fromUser(sender)
//                .attendanceRequestHistoryId(attendanceRequestHistory)
//                .notification(notification)
//                .build();
//        return userNotificationRepository.save(userNotification);
//    }
//
//    private AnnouncementNotification storeAnnouncementNotification(Announcement announcement, Notification notification) {
//        AnnouncementNotification announcementNotification = AnnouncementNotification.builder()
////                .toUser(user)
//                .announcement(announcement)
//                .notification(notification)
//                .build();
//        return announcementNotificationRepository.save(announcementNotification);
//    }
}
