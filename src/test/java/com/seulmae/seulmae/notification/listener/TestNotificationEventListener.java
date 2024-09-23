package com.seulmae.seulmae.notification.listener;

import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.announcement.repository.AnnouncementRepository;
import com.seulmae.seulmae.notification.entity.Notification;
import com.seulmae.seulmae.notification.event.AnnouncementNotificationEvent;
import com.seulmae.seulmae.notification.repository.NotificationRepository;
import com.seulmae.seulmae.notification.service.NotificationService;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.util.MockSetUpUtil;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class TestNotificationEventListener {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    protected MockSetUpUtil mockSetUpUtil;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected WorkplaceRepository workplaceRepository;

    @Autowired
    protected UserWorkplaceRepository userWorkplaceRepository;

    @MockBean
    private NotificationEventListener notificationEventListener;

    private User mockUser;
    private Workplace mockWorkplace;

    private UserWorkplace mockUserWorkplace;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .idUser(1L)
                .accountId("accountId")
                .build();
        userRepository.save(mockUser);

        mockWorkplace = Workplace.builder()
                .idWorkPlace(1L)
                .workplaceName("workplaceName")
                .build();
        workplaceRepository.save(mockWorkplace);

        mockUserWorkplace = UserWorkplace.builder()
                .user(mockUser)
                .workplace(mockWorkplace)
                .isManager(true)
                .build();
        userWorkplaceRepository.save(mockUserWorkplace);
    }

    @AfterEach
    void clear() {
        announcementRepository.deleteAll();
        userWorkplaceRepository.deleteAll();
        workplaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Notification 이벤트가 발행되면 이벤트가 동작한다")
    void whenNotificationEventPublished_thenTriggerEventListenerMethod() {
                Announcement announcement = Announcement.builder()
                .user(mockUser)
                .workplace(mockWorkplace)
                .title("title")
                .content("content")
                .isImportant(true)
                .views(0)
                .build();

                announcementRepository.saveAndFlush(announcement);

                AnnouncementNotificationEvent event = new AnnouncementNotificationEvent(announcement);

                eventPublisher.publishEvent(event);

                verify(notificationEventListener).handleNotificationEvent(event);
    }






//    @Test
//    @DisplayName("공지사항 생성 이벤트가 리스너에서 처리되는지 테스트")
//    void testAnnouncementNotificationEvent() {
//        // given: 공지사항 객체를 준비
//        Announcement announcement = Announcement.builder()
//                .user(mockUser)
//                .workplace(mockWorkplace)
//                .title("title")
//                .content("content")
//                .isImportant(true)
//                .views(0)
//                .build();
//
//        announcementRepository.saveAndFlush(announcement);
//
//        // when: 이벤트를 발행
//        eventPublisher.publishEvent(new AnnouncementNotificationEvent(announcement));
//
//        // then: 알림이 저장되었는지 확인 (리스너에서 처리되었는지 확인)
//        List<Notification> notifications = notificationRepository.findAll();
//        System.out.println("notifications = " + notifications);
//        assertThat(notifications).isNotEmpty();
//        assertThat(notifications.get(0).getMessage()).contains(announcement.getTitle());
//    }
}
