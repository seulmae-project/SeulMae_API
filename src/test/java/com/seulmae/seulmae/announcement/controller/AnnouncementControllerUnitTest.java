package com.seulmae.seulmae.announcement.controller;

import com.seulmae.seulmae.announcement.dto.request.AddAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.request.UpdateAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementDetailResponse;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.global.support.ControllerUnitTestSupport;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnnouncementControllerUnitTest extends ControllerUnitTestSupport {
    private static User mockUser;
    private static Workplace mockWorkplace;
    private final String URL = "/api/announcement/v1";

    @BeforeAll
    static void setUp() {
        mockUser = User.builder()
                .accountId("accountId")
                .build();

        mockWorkplace = Workplace.builder()
                .workplaceName("workplaceName")
                .build();
    }

    @Test
    @DisplayName("공지사항 생성한다")
    void createAnnouncement() throws Exception {
        AddAnnouncementRequest request = new AddAnnouncementRequest(mockWorkplace.getIdWorkPlace(), "title", "content", true);
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(user(mockUser))
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("공지사항 수정한다")
    void updateAnnouncement() throws Exception {
        Announcement announcement = new Announcement(mockUser, mockWorkplace, "title", "content", true);
        announcement.setIdAnnouncement(1L);
        UpdateAnnouncementRequest request = new UpdateAnnouncementRequest("updateTitle", "content", true);
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("announcementId", String.valueOf(announcement.getIdAnnouncement()))
                        .content(requestBody)
                        .with(user(mockUser))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공지사항 상세 정보 조회한다")
    void getAnnouncement() throws Exception {
        Announcement announcement = new Announcement(mockUser, mockWorkplace, "title", "content", true);
        announcement.setIdAnnouncement(1L);
        announcement.setRegDateAnnouncement(LocalDateTime.now());
        announcement.setRevisionDateAnnouncement(LocalDateTime.now());
        announcement.setViews(3);

        AnnouncementDetailResponse response = new AnnouncementDetailResponse(announcement);
        when(announcementService.getAnnouncement(anyLong(), any(User.class))).thenReturn(response);

        mockMvc.perform(get(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("announcementId", String.valueOf(announcement.getIdAnnouncement()))
                .with(user(mockUser))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("title"));
    }

    @Test
    @DisplayName("공지사항 전체 리스트를 조회한다")
    void getAnnouncements() {

    }

    @Test
    @DisplayName("필독 공지사항 리스트를 조회한다")
    void getImportantAnnouncements() {
    }

    @Test
    @DisplayName("메인 화면 공지사항 리스트를 조회한다")
    void getMainAnnouncements() {
    }

    @Test
    @DisplayName("공지사항 삭제한다")
    void deleteAnnouncement() {
    }
}