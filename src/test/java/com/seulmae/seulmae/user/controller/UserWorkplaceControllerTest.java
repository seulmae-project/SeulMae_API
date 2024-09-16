package com.seulmae.seulmae.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.util.MockSetUpUtil;
import com.seulmae.seulmae.wage.repository.WageRepository;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserWorkplaceControllerTest {
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
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private WageRepository wageRepository;

    @Autowired
    private WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;

    @Autowired
    private UserWorkScheduleRepository userWorkScheduleRepository;

    @Autowired
    private MockSetUpUtil mockSetUpUtil;

    private final String URL = "/api/workplace/user/v1";

    private User initUser;
    private Workplace initWorkplace;

    @BeforeEach
    public void mockMvcSetUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // 유저 생성
        String accountId = "test12345";
        String password = "qwer1234!";
        String phoneNumber = "01021231234";
        String birthday = "19990811";
        String name = "매니저인사람";
        boolean isMale = false;

        initUser = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        // 근무지 생성
        String workplaceName = "근무지";
        String mainAddress = "경기도";
        String subAddress = "안양시";
        String workplaceTel = "01015341234";

        initWorkplace = mockSetUpUtil.createWorkplace(workplaceName, mainAddress, subAddress, workplaceTel);
        mockSetUpUtil.createUserWorkplace(initUser, initWorkplace, true);
        mockSetUpUtil.createWorkSchedule(initWorkplace, "오전근무", LocalTime.of(9, 0), LocalTime.of(13, 0), List.of(1,2,3));
    }

    @AfterEach
    public void cleanUp() {
        userWorkScheduleRepository.deleteAll();
        workplaceJoinHistoryRepository.deleteAll();
        wageRepository.deleteAll();
        workScheduleRepository.deleteAll();
        userWorkplaceRepository.deleteAll();
        workplaceRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("근무지 내 유저정보 - 성공")
    void getUserInfoWithWorkplace() throws Exception {
//        Workplace workplace = workplaceRepository.findAll().getFirst();
        WorkSchedule workSchedule = workScheduleRepository.findAll().getFirst();

        String accountId = "test1234";
        String password = "qwer1234!";
        String phoneNumber = "01024231234";
        String name = "이름";
        String birthday = "19920103";
        Integer baseWage = 10000;
        Integer payday = 25;

        User mockUser = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, true);
        UserWorkplace userWorkplace = mockSetUpUtil.createUserWorkplace(mockUser, initWorkplace, false);
        mockSetUpUtil.createWorkplaceJoinHistoryWithApprove(initWorkplace, mockUser);
        mockSetUpUtil.createWage(mockUser, initWorkplace, baseWage, payday);
        mockSetUpUtil.createUserWorkSchedule(mockUser, workSchedule);

        mockMvc.perform(get(URL)
                .param("userWorkplaceId", String.valueOf(userWorkplace.getIdUserWorkplace()))
                .with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.payDay").value(payday))
                .andExpect(jsonPath("$.data.workScheduleDto.workScheduleTitle").value("오전근무"));


    }

    @Test
    @DisplayName("유저 근무지 탈퇴 - 성공")
    void withdrawWorkplace() throws Exception {

        User anotherMockUser = mockSetUpUtil.createUser("test1234", "qwer1234!", "01024231234", "이름", "19920103", true);

        UserWorkplace userWorkplace = mockSetUpUtil.createUserWorkplace(anotherMockUser, initWorkplace, false);

        mockMvc.perform(delete(URL)
                        .with(user(anotherMockUser))
                        .param("workplaceId", String.valueOf(initWorkplace.getIdWorkPlace())))
                .andDo(print())
                .andExpect(status().isOk());

        UserWorkplace deletedUserWorkplace = userWorkplaceRepository.findById(userWorkplace.getIdUserWorkplace())
                .orElse(null);
        assertThat(deletedUserWorkplace.getIsDelUserWorkplace()).isTrue();
    }
}