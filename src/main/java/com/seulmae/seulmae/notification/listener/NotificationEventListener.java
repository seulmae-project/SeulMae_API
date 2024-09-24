package com.seulmae.seulmae.notification.listener;

import com.seulmae.seulmae.global.exception.NotificationException;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.event.AnnouncementNotificationEvent;
import com.seulmae.seulmae.notification.event.MultiDeviceNotificationEvent;
import com.seulmae.seulmae.notification.event.NotificationEvent;
import com.seulmae.seulmae.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationEvent(NotificationEvent event) {

        switch (event.getType()) {
            case NotificationType.JOIN_RESPONSE:
            case NotificationType.JOIN_REQUEST:
            case NotificationType.ATTENDANCE_REQUEST:
            case NotificationType.ATTENDANCE_RESPONSE:
                MultiDeviceNotificationEvent multiDeviceNotificationEvent = (MultiDeviceNotificationEvent) event;
                try {
                    notificationService.sendMessageToUserWithMultiDevice(
                            multiDeviceNotificationEvent.getTitle(),
                            multiDeviceNotificationEvent.getBody(),
                            multiDeviceNotificationEvent.getReceiver(),
                            multiDeviceNotificationEvent.getType(),
                            multiDeviceNotificationEvent.getId(),
                            multiDeviceNotificationEvent.getWorkplaceId()
                    );
                } catch (NotificationException e) {
                    log.error("Failed to send notification for event type: {}, title: {}, body: {}",
                            multiDeviceNotificationEvent.getType(), multiDeviceNotificationEvent.getTitle(), multiDeviceNotificationEvent.getBody(), e);
                    throw new NotificationException("알림 실패: [" + multiDeviceNotificationEvent.getType() + "] '" + multiDeviceNotificationEvent.getTitle() + "', ErrorMessage: " + e.getMessage());
                }
                break;

            case NotificationType.NOTICE:
                AnnouncementNotificationEvent announcementNotificationEvent = (AnnouncementNotificationEvent) event;
                try {
                    notificationService.sendMessageToUsersAboutAnnouncement(announcementNotificationEvent.getAnnouncement());

                } catch (NotificationException e) {
                    log.error("Failed to send notification for event type: {}, message: {}",
                            announcementNotificationEvent.getType(), e);
                    throw new NotificationException("알림 실패: [" + announcementNotificationEvent.getType() + "] " + " ErrorMessage: " + e.getMessage());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown Notification Event Type: " + event.getType());
        }
    }

//
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
////    @EventListener
//    public void testListener(AnnouncementNotificationEvent event) {
//        System.out.println("테스트 이벤트가 성공적으로 발생했습니다!!!!!!!");
//        System.out.println("title!!!!!!!!!!!!!: " + event.getAnnouncement().getTitle());
//    }
}
