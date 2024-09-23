package com.seulmae.seulmae.announcement.service;

import com.seulmae.seulmae.announcement.dto.response.AnnouncementDetailResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementListResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.global.support.ServiceTestSupport;
import com.seulmae.seulmae.notification.entity.Notification;
import com.seulmae.seulmae.notification.event.AnnouncementNotificationEvent;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.workplace.entity.Workplace;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnnouncementServiceTest extends ServiceTestSupport {
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

//    @Test
//    @DisplayName("공지사항을 생성한다")
//    void createAnnouncement() {
////        Announcement announcement = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title1", "content1", true);
////        announcementRepository.save(announcement);
//        AddAnnouncementRequest request = new AddAnnouncementRequest(mockWorkplace.getIdWorkPlace(), "title", "content", true);
//
//        announcementService.createAnnouncement(request, mockUser);
//
//        Announcement announcement = announcementRepository.findAll().getFirst();
//        assertThat(announcement.getContent()).isEqualTo("content");
//
//        // 2. 이벤트가 발행되었는지 확인
//        verify(notificationService).sendMessageToUsersAboutAnnouncement(announcement);
//
//        // 3. Notification이 DB에 저장되었는지 확인
//        Notification notification = notificationRepository.findAll().getFirst();
//        assertThat(notification).isNotNull();
//        assertThat(notification.getMessage()).contains("title");
//        System.out.println("notification = " + notification);
//    }



    @Test
    void updateAnnouncement() {
    }

    @Test
    @DisplayName("공지사항을 단일 조회한다")
    void getAnnouncement() {
        Announcement announcement = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title", "content", true);
        AnnouncementDetailResponse response = announcementService.getAnnouncement(announcement.getIdAnnouncement(), announcement.getUser());

        assertThat(response.getTitle()).isEqualTo("title");
    }

    @Test
    void countViews() {
    }

    @Test
    @DisplayName("특정 근무지의 전체 공지사항을 조회한다")
    void getAnnouncements() {
        Announcement announcement1 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title1", "content1", true);
        Announcement announcement2 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title2", "content2", false);

        JSONObject jsonObject = (JSONObject) announcementService.getAnnouncements(mockWorkplace.getIdWorkPlace(), mockUser, 0, 50);
        List<AnnouncementListResponse> responses = (List<AnnouncementListResponse>) jsonObject.get("data");
        assertThat(responses)
                .hasSize(2);
    }


    @Test
    @DisplayName("특정 근무지의 메인 화면 전체 공지사항을 조회한다")
    void getMainAnnouncements() {
        Announcement announcement1 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title1", "content1", true);
        Announcement announcement2 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title2", "content2", false);
        Announcement announcement3 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title3", "content3", false);
        Announcement announcement4 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title4", "content4", false);
        Announcement announcement5 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title5", "content5", false);
        Announcement announcement6 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title6", "content6", false);

        List<AnnouncementMainListResponse> responses =  announcementService.getMainAnnouncements(mockWorkplace.getIdWorkPlace(), mockUser);

        assertThat(responses)
                .hasSize(5);
    }

    @Test
    @DisplayName("특정 근무지의 필독 공지사항을 전체 조회한다")
    void getImportantAnnouncements() {
        Announcement announcement1 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title1", "content1", true);
        Announcement announcement2 = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title2", "content2", false);

        List<AnnouncementMainListResponse> responses =  announcementService.getImportantAnnouncements(mockWorkplace.getIdWorkPlace(), mockUser);

        assertThat(responses)
                .hasSize(1);
    }

    @Test
    @DisplayName("공지사항을 삭제한다")
    void deleteAnnouncement() {
        Announcement announcement = mockSetUpUtil.createAnnouncement(mockUser, mockWorkplace, "title1", "content1", true);

        announcementService.deleteAnnouncement(announcement.getIdAnnouncement(), mockUser);

        assertThat(announcement.getIsDelAnnouncement()).isTrue();
    }

    @Test
    @DisplayName("매니저 권한이 없어서 공지사항 삭제에 실패한다")
    void deleteAnnouncementFailNoAuth() {
        User noAuthUser = userRepository.save(User.builder().build());
        userWorkplaceRepository.save(UserWorkplace.builder().user(noAuthUser).workplace(mockWorkplace).isManager(false).build());

        Announcement announcement = mockSetUpUtil.createAnnouncement(noAuthUser, mockWorkplace, "title1", "content1", true);

        assertThatThrownBy(() -> announcementService.deleteAnnouncement(announcement.getIdAnnouncement(), noAuthUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 근무지의 매니저가 아닙니다.");
    }
}