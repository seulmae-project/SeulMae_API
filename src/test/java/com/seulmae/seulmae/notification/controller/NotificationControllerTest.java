package com.seulmae.seulmae.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.announcement.dto.request.AddAnnouncementRequest;
import com.seulmae.seulmae.announcement.repository.AnnouncementRepository;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.entity.Notification;
import com.seulmae.seulmae.notification.repository.NotificationRepository;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.util.AuthenticationHelper;
import com.seulmae.seulmae.util.MockSetUpUtil;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private UserWorkplaceRepository userWorkplaceRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MockSetUpUtil mockSetUpUtil;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    private final String URL = "/api/notification/v1";

    @BeforeEach
    public void mockMvcSetUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        String accountId = "test1234";
        String password = "qwer1234!";
        String phoneNumber = "01024231234";
        String name = "이름";
        String birthday = "19920103";
        boolean isMale = true;

        User mockUser = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        String workplaceName = "근무지";
        String mainAddress = "경기도";
        String subAddress = "안양시";
        String workplaceTel = "01015341234";

        Workplace mockWorkplace = mockSetUpUtil.createWorkplace(workplaceName, mainAddress, subAddress, workplaceTel);
        mockSetUpUtil.createUserWorkplace(mockUser, mockWorkplace, true);

        String announcementURL = "/api/announcement/v1";
        String title = "첫공지";
        String content = "내용";
        boolean isImportant = true;

        AddAnnouncementRequest addAnnouncementRequest = new AddAnnouncementRequest(mockWorkplace.getIdWorkPlace(), title, content, isImportant);
        String announcementRequest = objectMapper.writeValueAsString(addAnnouncementRequest);

        mockMvc.perform(post(announcementURL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(announcementRequest))
                .andDo(print());

    }

    @AfterEach
    public void cleanUp() {
        notificationRepository.deleteAll();
        announcementRepository.deleteAll();
        userWorkplaceRepository.deleteAll();
        workplaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유저별 알림 리스트 - 성공")
    void getNotifications() throws Exception {
        UserWorkplace userWorkplace = userWorkplaceRepository.findAll().getFirst();

        mockMvc.perform(get(URL + "/list")
                        .param("userWorkplaceId", String.valueOf(userWorkplace.getIdUserWorkplace()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].notificationType").value(NotificationType.NOTICE.name()));
    }
}