package com.seulmae.seulmae.notification.service;

import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.dto.request.FcmSendRequest;
import com.seulmae.seulmae.notification.entity.AnnouncementNotification;
import com.seulmae.seulmae.notification.entity.Notification;
import com.seulmae.seulmae.notification.entity.UserNotification;
import com.seulmae.seulmae.notification.repository.AnnouncementNotificationRepository;
import com.seulmae.seulmae.notification.repository.FcmTokenRepository;
import com.seulmae.seulmae.notification.repository.NotificationRepository;
import com.seulmae.seulmae.notification.repository.UserNotificationRepository;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final FcmService fcmService;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final AnnouncementNotificationRepository announcementNotificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final WorkplaceRepository workplaceRepository;

    private final String TOPIC_PREFIX = "workplace";
    // 공지사항 - 해당 근무지 소속 전체 유저에게 발송
    @Transactional
    public void sendMessageToUsersAboutAnnouncement(Announcement announcement) {
        Workplace workplace = announcement.getWorkplace();
        String topic = workplace.getWorkplaceTopic();

        if (topic == null) {
            topic = TOPIC_PREFIX + UUID.randomUUID().toString().replace("-","");
            workplace.setWorkplaceTopic(topic);
            workplaceRepository.save(workplace);
        }

        String title = "[공지사항] '" + announcement.getTitle() + "'이 등록되었습니다.";
        String body = announcement.getContent();

        fcmService.sendTopicMessageTo(topic, title, body);

        Notification notification = storeNotification(title, body, NotificationType.NOTICE);
        storeAnnouncementNotification(announcement, notification);
    }


    @Transactional
    public void sendMessageToManagerForAttendanceRequest(FcmSendRequest request, User sender) throws IOException {
        // 해당 fcm 토큰이 존재하는 지 여부 확인하기
        User receiver = fcmTokenRepository.findUserByFcmToken(request.getFcmToken())
                .orElseThrow(() -> new NoSuchElementException("There is no user's FcmToken on Server"));

        Long attendanceRequestHistory = null; // 어떻게 받아내...?

        // 메세지 보내기
        fcmService.sendMessageTo(request.getFcmToken(), request.getTitle(), request.getBody());

        // notification 저장하기
        Notification notification = storeNotification(request.getTitle(), request.getBody(), NotificationType.ATTENDANCE_REQUEST);
        // userNotification 저장하기
        storeUserNotification(sender, receiver, attendanceRequestHistory, notification, false);
    }

    @Transactional
    public void sendMessageToAlbaForAttendanceResponse(FcmSendRequest request, User sender) throws IOException {
        User receiver = fcmTokenRepository.findUserByFcmToken(request.getFcmToken())
                .orElseThrow(() -> new NoSuchElementException("There is no user's FcmToken on Server"));

        Long attendanceRequestHistory = null; // 어떻게 받아내...?

        // 메세지 보내기
        fcmService.sendMessageTo(request.getFcmToken(), request.getTitle(), request.getBody());

        // notification 저장하기
        Notification notification = storeNotification(request.getTitle(), request.getBody(), NotificationType.ATTENDANCE_RESPONSE);
        // userNotification 저장하기
        storeUserNotification(sender, receiver, attendanceRequestHistory, notification, false);
    }




    public Notification storeNotification(String title, String message, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .build();
        return notificationRepository.save(notification);
    }

    private UserNotification storeUserNotification(User sender, User receiver, Long attendanceRequestHistory, Notification notification, boolean isRead) {
        UserNotification userNotification = UserNotification.builder()
                .toUser(receiver)
                .fromUser(sender)
                .attendanceRequestHistoryId(attendanceRequestHistory)
                .notification(notification)
                .isRead(isRead)
                .build();
        return userNotificationRepository.save(userNotification);
    }

    private AnnouncementNotification storeAnnouncementNotification(Announcement announcement, Notification notification) {
        AnnouncementNotification announcementNotification = AnnouncementNotification.builder()
//                .toUser(user)
                .announcement(announcement)
                .notification(notification)
                .build();
        return announcementNotificationRepository.save(announcementNotification);
    }
}
