package com.seulmae.seulmae.announcement.controller;

import com.seulmae.seulmae.announcement.dto.request.AddAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.request.UpdateAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementDetailResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementListResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.global.support.ControllerUnitTestSupport;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
                .idUser(1L)
                .accountId("accountId")
                .build();

        mockWorkplace = Workplace.builder()
                .idWorkPlace(1L)
                .workplaceName("workplaceName")
                .build();
    }

    @Test
    @DisplayName("공지사항 생성한다")
    void createAnnouncement() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new AddAnnouncementRequest());

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(user(mockUser))
        )
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("공지사항 생성: 권한 실패")
    void createAnnouncementFailNoAuth() throws Exception {
        String requestBody =  objectMapper.writeValueAsString(new AddAnnouncementRequest());
        doThrow(new IllegalArgumentException("해당 근무지의 매니저가 아닙니다."))
                .when(announcementService).createAnnouncement(any(AddAnnouncementRequest.class), any(User.class));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(mockUser))
                )
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("공지사항 수정한다")
    void updateAnnouncement() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new UpdateAnnouncementRequest());

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("announcementId", String.valueOf(1L))
                        .content(requestBody)
                        .with(user(mockUser))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공지사항 상세 정보 조회한다")
    void getAnnouncement() throws Exception {
        Announcement announcement = Announcement.builder()
                .idAnnouncement(1L)
                .user(mockUser)
                .workplace(mockWorkplace)
                .title("title")
                .content("content")
                .isImportant(true)
                .views(3)
                .isDelAnnouncement(false)
                .regDateAnnouncement(LocalDateTime.now())
                .revisionDateAnnouncement(LocalDateTime.now())
                .build();

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
    void getAnnouncements() throws Exception {
        Announcement announcement1 = Announcement.builder()
                .idAnnouncement(1L)
                .user(mockUser)
                .workplace(mockWorkplace)
                .title("title")
                .content("content")
                .isImportant(true)
                .views(3)
                .isDelAnnouncement(false)
                .regDateAnnouncement(LocalDateTime.now())
                .revisionDateAnnouncement(LocalDateTime.now())
                .build();

        Announcement announcement2 = Announcement.builder()
                .idAnnouncement(2L)
                .user(mockUser)
                .workplace(mockWorkplace)
                .title("title2")
                .content("content2")
                .isImportant(false)
                .views(3)
                .isDelAnnouncement(false)
                .regDateAnnouncement(LocalDateTime.now())
                .revisionDateAnnouncement(LocalDateTime.now())
                .build();

        Object response = List.of(new AnnouncementListResponse(announcement1), new AnnouncementListResponse(announcement2));
        when(announcementService.getAnnouncements(anyLong(), any(User.class), anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get(URL + "/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("workplaceId", String.valueOf(mockWorkplace.getIdWorkPlace()))
                        .with(user(mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("title"))
                .andExpect(jsonPath("$.data[1].title").value("title2"));

    }

    @Test
    @DisplayName("필독 공지사항 리스트를 조회한다")
    void getImportantAnnouncements() throws Exception {
        List<AnnouncementMainListResponse> response = List.of(new AnnouncementMainListResponse(1L, "title"));
        when(announcementService.getImportantAnnouncements(anyLong(), any(User.class))).thenReturn(response);

        mockMvc.perform(get(URL + "/list/important")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("workplaceId", String.valueOf(mockWorkplace.getIdWorkPlace()))
                        .with(user(mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("title"));
    }

    @Test
    @DisplayName("메인 화면 공지사항 리스트를 조회한다")
    void getMainAnnouncements() throws Exception {
        List<AnnouncementMainListResponse> response = List.of(new AnnouncementMainListResponse(1L, "title"));
        when(announcementService.getMainAnnouncements(anyLong(), any(User.class))).thenReturn(response);

        mockMvc.perform(get(URL + "/main")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("workplaceId", String.valueOf(mockWorkplace.getIdWorkPlace()))
                        .with(user(mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("title"));
    }

    @Test
    @DisplayName("공지사항 삭제한다")
    void deleteAnnouncement() throws Exception {
         doNothing().when(announcementService).deleteAnnouncement(anyLong(), any(User.class));

        mockMvc.perform(delete(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("announcementId", String.valueOf(1L))
                        .with(user(mockUser))
                )
                .andExpect(status().isOk());
    }
}