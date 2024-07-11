package com.seulmae.seulmae.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.dto.request.UserWorkScheduleAddRequest;
import com.seulmae.seulmae.user.dto.request.UserWorkScheduleUpdateRequest;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserWorkScheduleControllerTest {
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
    private UserWorkScheduleRepository userWorkScheduleRepository;

    private final String URL = "/api/user/schedule/v1";

    @BeforeEach
    public void mockMvcSetUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        String accountId = "test1234";
        String password = "qwer1234!";
        String phoneNumber = "01024231234";
        String name = "이름";
        String birthday = "19920103";
        boolean isMale = true;

        // 사용자 객체 생성
        User mockUser = createUser(accountId, password, phoneNumber, name, birthday, isMale);

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
        MockMultipartFile multipartFile = new MockMultipartFile("workplaceAddDto", "workplaceAddDto", "application/json", request.getBytes());

        ResultActions result = mockMvc.perform(multipart(url)
                .file(multipartFile)
                .with(_request -> {
                    _request.setMethod("POST");
                    return _request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON));

        final Workplace workplace = workplaceRepository.findAll().get(0);
        final String workScheduleTitle = "오전일정";
        final LocalTime startTime = LocalTime.of(9, 0);
        final LocalTime endTime = LocalTime.of(13, 0);
        final List<Integer> days = List.of(1, 2, 3);

        createWorkSchedule(workplace, workScheduleTitle, startTime, endTime, days);
    }

    @AfterEach
    public void cleanUp() {
        userWorkScheduleRepository.deleteAll();
        workScheduleRepository.deleteAll();
        userWorkplaceRepository.deleteAll();
        workplaceRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("해당 근무일정의 유저 리스트 조회 - 성공")
    void getUsersByWorkSchedule() throws Exception {
        String accountId = "test5555";
        String password = "qwer1234!";
        String phoneNumber = "01034231234";
        String name = "이름2";
        String birthday = "19930103";
        boolean isMale = false;

        User targetUser = createUser(accountId, password, phoneNumber, name, birthday, isMale);
        WorkSchedule workSchedule = workScheduleRepository.findAll().get(0);
        Workplace workplace = workplaceRepository.findAll().get(0);
        createUserWorkplaceNotManager(targetUser, workplace);
        UserWorkSchedule userWorkSchedule = createUserWorkSchedule(targetUser, workSchedule);

        ResultActions result = mockMvc.perform(get(URL + "/list")
                .param("workScheduleId", String.valueOf(workSchedule.getIdWorkSchedule())));

        System.out.println(result.andReturn().getResponse().getContentAsString());
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userName").value(targetUser.getName()))
                .andExpect(jsonPath("$.data[0].userWorkScheduleId").value(userWorkSchedule.getIdUserWorkSchedule()));

    }

    @Test
    @DisplayName("유저 근무일정 추가 - 성공")
    @Transactional
    void addUserWorkSchedule() throws Exception {
        String accountId = "test5555";
        String password = "qwer1234!";
        String phoneNumber = "01034231234";
        String name = "이름2";
        String birthday = "19930103";
        boolean isMale = false;

        User targetUser = createUser(accountId, password, phoneNumber, name, birthday, isMale);
        WorkSchedule workSchedule = workScheduleRepository.findAll().get(0);
        Workplace workplace = workplaceRepository.findAll().get(0);
        createUserWorkplaceNotManager(targetUser, workplace);

        UserWorkScheduleAddRequest userWorkScheduleAddRequest = new UserWorkScheduleAddRequest(targetUser.getIdUser(), workSchedule.getIdWorkSchedule());
        String request = objectMapper.writeValueAsString(userWorkScheduleAddRequest);

        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));


        result.andExpect(status().isCreated());
        UserWorkSchedule userWorkSchedule = userWorkScheduleRepository.findAll().get(0);
        assertThat(userWorkSchedule.getWorkSchedule()).isEqualTo(workSchedule);
        assertThat(userWorkSchedule.getUser()).isEqualTo(targetUser);

    }

    @Test
    @DisplayName("유저 근무일정 수정 - 성공")
    @Transactional
    void modifyUserWorkSchedule() throws Exception {
        String accountId = "test5555";
        String password = "qwer1234!";
        String phoneNumber = "01034231234";
        String name = "이름2";
        String birthday = "19930103";
        boolean isMale = false;

        User targetUser = createUser(accountId, password, phoneNumber, name, birthday, isMale);
        WorkSchedule workSchedule = workScheduleRepository.findAll().get(0);
        Workplace workplace = workplaceRepository.findAll().get(0);
        createUserWorkplaceNotManager(targetUser, workplace);
        UserWorkSchedule userWorkSchedule = createUserWorkSchedule(targetUser, workSchedule);

        final String workScheduleTitle = "오후일정";
        final LocalTime startTime = LocalTime.of(13, 0);
        final LocalTime endTime = LocalTime.of(19, 0);
        final List<Integer> days = List.of(1, 2, 3);

        WorkSchedule newWorkSchedule = createWorkSchedule(workplace, workScheduleTitle, startTime, endTime, days);

        UserWorkScheduleUpdateRequest userWorkScheduleUpdateRequest = new UserWorkScheduleUpdateRequest(newWorkSchedule.getIdWorkSchedule());
        String request = objectMapper.writeValueAsString(userWorkScheduleUpdateRequest);

        ResultActions result = mockMvc.perform(patch(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request)
                .param("userWorkScheduleId", String.valueOf(userWorkSchedule.getIdUserWorkSchedule())));

        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isCreated());
        UserWorkSchedule updatedUserWorkSchedule = userWorkScheduleRepository.findAll().get(0);
        assertThat(updatedUserWorkSchedule.getWorkSchedule()).isEqualTo(newWorkSchedule);

    }

    @Test
    @DisplayName("유저 근무일정 삭제 - 성공")
    void deleteUserWorkSchedule() throws Exception {
        String accountId = "test5555";
        String password = "qwer1234!";
        String phoneNumber = "01034231234";
        String name = "이름2";
        String birthday = "19930103";
        boolean isMale = false;

        User targetUser = createUser(accountId, password, phoneNumber, name, birthday, isMale);
        WorkSchedule workSchedule = workScheduleRepository.findAll().get(0);
        Workplace workplace = workplaceRepository.findAll().get(0);
        createUserWorkplaceNotManager(targetUser, workplace);
        UserWorkSchedule userWorkSchedule = createUserWorkSchedule(targetUser, workSchedule);

        ResultActions result = mockMvc.perform(delete(URL)
                .param("userWorkScheduleId", String.valueOf(userWorkSchedule.getIdUserWorkSchedule())));

        result.andExpect(status().isNoContent());
        List<UserWorkSchedule> userWorkSchedules = userWorkScheduleRepository.findAll();
        assertThat(userWorkSchedules.size()).isZero();
    }

    private WorkSchedule createWorkSchedule(Workplace workplace, String workScheduleTitle, LocalTime startTime, LocalTime endTime, List<Integer> days) {
        WorkSchedule workSchedule = WorkSchedule.builder()
                .workScheduleTitle(workScheduleTitle)
                .workplace(workplace)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        List<WorkScheduleDay> workScheduleDays = days.stream()
                .map(dayInt -> {
                    Day day = Day.fromInt(dayInt);
                    return WorkScheduleDay.builder()
                            .workSchedule(workSchedule)
                            .day(day)
                            .build();
                }).collect(Collectors.toList());

        workSchedule.setWorkScheduleDays(workScheduleDays);

        return workScheduleRepository.save(workSchedule);

        }

    private User createUser(String accountId, String password, String phoneNumber, String name, String birthday, Boolean isMale) {

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
        return mockUser;
    }

    private void createUserWorkplaceNotManager(User targetUser, Workplace workplace) {
        userWorkplaceRepository.save(UserWorkplace.builder()
                .user(targetUser)
                .workplace(workplace)
                .isManager(false)
                .build());
    }

    private UserWorkSchedule createUserWorkSchedule(User targetUser, WorkSchedule workSchedule) {
        return userWorkScheduleRepository.save(
                UserWorkSchedule.builder()
                        .user(targetUser)
                        .workSchedule(workSchedule)
                        .build()
        );
    }
}