package com.seulmae.seulmae.notification.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.dto.response.NotificationResponse;
import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.notification.entity.Notification;
import com.seulmae.seulmae.notification.repository.FcmTokenRepository;
import com.seulmae.seulmae.notification.repository.NotificationRepository;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.user.service.UserService;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import com.seulmae.seulmae.workplace.service.WorkplaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final WorkplaceRepository workplaceRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

    private final FcmIndividualServiceImpl fcmIndividualServiceImpl;
    private final FcmTopicServiceImpl fcmTopicServiceImpl;
    private final FindByIdUtil findByIdUtil;
    private final UserService userService;
    private final WorkplaceService workplaceService;

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

        for (User user : users) {
            UserWorkplace userWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(user, workplace)
                            .orElseThrow(() -> new NoSuchElementException("해당 User 및 Workplace와 관련된 UserWorkplace가 존재하지 않습니다."));
            storeNotification(title, body, userWorkplace, NotificationType.NOTICE);
        }

        fcmTopicServiceImpl.sendMessageTo(topic, title, body, NotificationType.NOTICE, announcement.getIdAnnouncement());

        log.info("Sending announcement notification to topic: {}", topic);
    }


    /**
     * 일대일(여러 기기) 메세지 전송
     */
    @Transactional
    public void sendMessageToUserWithMultiDevice(String title, String body, User receiver, NotificationType type, Long id, Long workplaceId) {
        try {
            List<String> fcmTokens = receiver.getFcmTokens().stream()
                    .map(FcmToken::getFcmToken)
                    .toList();
            Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);
            UserWorkplace userWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(receiver, workplace)
                    .orElseThrow(() -> new NoSuchElementException("해당 User 및 Workplace와 관련된 UserWorkplace가 존재하지 않습니다."));

            storeNotification(title, body, userWorkplace, type);

            fcmIndividualServiceImpl.sendMultiMessageTo(fcmTokens, title, body, type, id, workplaceId);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message to user {} with title '{}'", receiver.getUsername(), title, e);

            throw new RuntimeException("Failed to send FCM message", e);
        } catch (Exception e) {
            log.error("An error occurred while sending message to user {} with title '{}'", receiver.getUsername(), title, e);
            throw new RuntimeException("Failed to send message", e);
        }
    }



    public void storeNotification(String title, String message, UserWorkplace userWorkplace, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .userWorkplace(userWorkplace)
                .notificationType(notificationType)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotifications(Long userWorkplaceId, HttpServletRequest request) {
        UserWorkplace userWorkplace = findByIdUtil.getUserWorkplaceById(userWorkplaceId);
        return notificationRepository.findAllByUserWorkplace(userWorkplace).stream()
                .map(notification -> {
                    String imageURL = switch (notification.getNotificationType()) {
                        case NOTICE ->
                                workplaceService.getWorkplaceImageUrlList(userWorkplace.getWorkplace(), request).getFirst();
                        case ATTENDANCE_REQUEST, ATTENDANCE_RESPONSE, JOIN_REQUEST, JOIN_RESPONSE ->
                                userService.getUserImageURL(userWorkplace.getUser(), request);
                        default -> throw new NoSuchElementException("관련 로직이 아직 존재하지 않음.");
                    };
                    NotificationResponse notificationResponse = new NotificationResponse(notification);
                    notificationResponse.setImageURL(imageURL);
                    return notificationResponse;
                })
                .collect(Collectors.toList());
    }
}
