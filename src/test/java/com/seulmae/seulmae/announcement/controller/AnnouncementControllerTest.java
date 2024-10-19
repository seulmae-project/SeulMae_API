package com.seulmae.seulmae.announcement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.announcement.dto.request.AddAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.request.UpdateAnnouncementRequest;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.announcement.repository.AnnouncementRepository;
import com.seulmae.seulmae.notification.repository.NotificationRepository;
import com.seulmae.seulmae.user.enums.Role;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.util.AuthenticationHelper;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnnouncementControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private UserWorkplaceRepository userWorkplaceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @BeforeEach
    public void mockMvcSetUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // 사용자 정보 설정
        String accountId = "test1234";
        String password = "qwer1234!";
        String phoneNumber = "01024231234";
        String name = "이름";
        String birthday = "19920103";
        boolean isMale = true;

        // 사용자 객체 생성
        User mockUser = userRepository.save(User.builder()
                .accountId(accountId)
                .password(password)
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(isMale)
                .birthday(birthday)
                .authorityRole(Role.USER)
                .build());

        // SecurityContext 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUser, mockUser.getPassword(), AuthorityUtils.createAuthorityList(String.valueOf(Role.USER)));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);


        String url = "/api/workplace/v1/add";

        String workplaceName = "근무지";
        String mainAddress = "경기도";
        String subAddress = "안양시";
        String workplaceTel = "01015341234";

        WorkplaceAddDto workplaceAddDto = new WorkplaceAddDto(workplaceName, mainAddress, subAddress, workplaceTel);
        String request = objectMapper.writeValueAsString(workplaceAddDto);
        MockMultipartFile multipartFile = new MockMultipartFile("workplaceAddDto", "workplaceAddDto", "application/json; charset=UTF-8", request.getBytes(StandardCharsets.UTF_8));

        ResultActions result = mockMvc.perform(multipart(url)
                .file(multipartFile)
                .with(_request -> {
                    _request.setMethod("POST");
                    return _request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON));

        System.out.println(result.andReturn().getResponse().getContentAsString());
    }

    @AfterEach
    public void cleanUp() {
        announcementRepository.deleteAll();
        notificationRepository.deleteAll();
        workplaceJoinHistoryRepository.deleteAll();
        userWorkplaceRepository.deleteAll();
        workplaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("공지사항 생성 - 성공")
    void createAnnouncement() throws Exception {
        String url = "/api/announcement/v1";
        String title = "첫공지";
        String content = "내용";
        boolean isImportant = true;
        Workplace workplace = workplaceRepository.findAll().get(0);
        AddAnnouncementRequest addAnnouncementRequest = new AddAnnouncementRequest(workplace.getIdWorkPlace(), title, content, isImportant);
        String request = objectMapper.writeValueAsString(addAnnouncementRequest);

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request));

        System.out.println(result.andReturn().getResponse().getContentAsString());

        result.andExpect(status().isCreated());
        Announcement announcement = announcementRepository.findAll().get(0);
        assertThat(announcement.getContent()).isEqualTo(content);
        assertThat(announcement.getTitle()).isEqualTo(title);

    }

    @Test
    @DisplayName("공지사항 수정 - 성공")
    void updateAnnouncement() throws Exception {
        String url = "/api/announcement/v1";
        String title = "첫공지";
        String content = "내용";
        boolean isImportant = true;

        String changeTitle = "변한공지";

        Workplace workplace = workplaceRepository.findAll().get(0);
        User loginUser = authenticationHelper.getCurrentUser();
        Announcement announcement = new Announcement(loginUser, workplace, title, content, isImportant);
        announcementRepository.save(announcement);

        String request = objectMapper.writeValueAsString(
                UpdateAnnouncementRequest.builder()
                        .title(changeTitle)
                        .build());

        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request)
                .param("announcementId", String.valueOf(announcement.getIdAnnouncement())));


        result.andExpect(status().isOk());

        Announcement updatedAnnouncement = announcementRepository.findById(announcement.getIdAnnouncement())
                .orElseThrow(() -> new NoSuchElementException("공지사항 없음"));
        assertThat(updatedAnnouncement.getTitle()).isEqualTo(changeTitle);
    }

    @Test
    @DisplayName("공지 조회 - 성공")
    void getAnnouncement() throws Exception {
        String url = "/api/announcement/v1";
        String title = "첫공지";
        String content = "내용";
        boolean isImportant = true;

        Workplace workplace = workplaceRepository.findAll().get(0);
        User loginUser = authenticationHelper.getCurrentUser();
        Announcement announcement = announcementRepository.save(new Announcement(loginUser, workplace, title, content, isImportant));

        ResultActions result = mockMvc.perform(get(url)
                .param("announcementId", String.valueOf(announcement.getIdAnnouncement())));

        System.out.println(result.andReturn().getResponse().getContentAsString());

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(title));

        //[TODO] View의 경우 redis랑 엮여있는데, 테스트 데이터와 진짜 데이터를 어떻게 구분하지?
    }

    @Test
    @DisplayName("공지 전체 조회 - 성공")
    void getAnnouncements() throws Exception {
        String url = "/api/announcement/v1/list";
        String title = "첫공지";
        String content = "내용";
        boolean isImportant = true;

        Workplace workplace = workplaceRepository.findAll().get(0);
        User loginUser = authenticationHelper.getCurrentUser();
        announcementRepository.save(new Announcement(loginUser, workplace, title, content, isImportant));

        ResultActions resultActions = mockMvc.perform(get(url)
                .param("workplaceId", String.valueOf(workplace.getIdWorkPlace())));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].title").value(title));

    }

//    @Test
//    void getImportantAnnouncements() {
//    }
//
//    @Test
//    void getMainAnnouncements() {
//    }

    @Test
    @DisplayName("공지 삭제 - 성공")
    void deleteAnnouncement() throws Exception {
        String url = "/api/announcement/v1";
        String title = "첫공지";
        String content = "내용";
        boolean isImportant = true;

        Workplace workplace = workplaceRepository.findAll().get(0);
        User loginUser = authenticationHelper.getCurrentUser();
        Announcement announcement = announcementRepository.save(new Announcement(loginUser, workplace, title, content, isImportant));

        ResultActions resultActions = mockMvc.perform(delete(url)
                .param("announcementId", String.valueOf(announcement.getIdAnnouncement())));

        resultActions.andExpect(status().isOk());

        Announcement deletedAnnouncement = announcementRepository.findById(announcement.getIdAnnouncement()).get();
        assertThat(deletedAnnouncement.getIsDelAnnouncement()).isTrue();
    }
}