package com.seulmae.seulmae.workplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.dto.WorkScheduleAddDto;
import com.seulmae.seulmae.workplace.dto.WorkScheduleUpdateDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkScheduleDayRepository;
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
class WorkScheduleControllerTest {
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
    private WorkScheduleDayRepository workScheduleDayRepository;

    private final String URL = "/api/schedule/v1";

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
        MockMultipartFile multipartFile = new MockMultipartFile("workplaceAddDto", "workplaceAddDto", "application/json", request.getBytes());

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
        workScheduleRepository.deleteAll();
        userWorkplaceRepository.deleteAll();
        workplaceRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @Transactional
    @DisplayName("근무일정 생성 - 성공")
    void addWorkSchedule() throws Exception {
        final Long workPlaceId = workplaceRepository.findAll().get(0).getIdWorkPlace();
        final String workScheduleTitle = "오전일정";
        final LocalTime startTime = LocalTime.of(9, 0);
        final LocalTime endTime = LocalTime.of(13, 0);
        final List<Integer> days = List.of(1, 2, 3);
        WorkScheduleAddDto workScheduleAddDto = new WorkScheduleAddDto(workPlaceId, workScheduleTitle, startTime, endTime, days);
        String request = objectMapper.writeValueAsString(workScheduleAddDto);

        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        result.andExpect(status().isCreated());
        WorkSchedule workSchedule = workScheduleRepository.findAll().get(0);
        Integer workScheduleDayLength = workScheduleDayRepository.findAllByWorkSchedule(workSchedule).size();
        assertThat(workSchedule.getWorkScheduleTitle()).isEqualTo(workScheduleTitle);
        assertThat(workSchedule.getStartTime()).isEqualTo(startTime);
        assertThat(workSchedule.getWorkScheduleDays().size()).isEqualTo(workScheduleDayLength);
    }

    @Test
    @DisplayName("근무일정 수정 - 성공")
    @Transactional
    void updateWorkSchedule() throws Exception {
        final String workScheduleTitle = "수정일정";
        final List<Integer> days = List.of(5, 6);
        final Boolean isActive = false;
        WorkSchedule workSchedule = createWorkSchedule();

        WorkScheduleUpdateDto workScheduleUpdateDto = WorkScheduleUpdateDto.builder()
                .workScheduleTitle(workScheduleTitle)
                .days(days)
                .isActive(isActive)
                .build();
        String request = objectMapper.writeValueAsString(workScheduleUpdateDto);

        ResultActions result = mockMvc.perform(put(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request)
                .param("workScheduleId", String.valueOf(workSchedule.getIdWorkSchedule())));

        result.andExpect(status().isOk());

        List<Day> daysEnum = days.stream().map(Day::fromInt).collect(Collectors.toList());
        WorkSchedule updatedWorkSchedule = workScheduleRepository.findById(workSchedule.getIdWorkSchedule()).get();
        List<Day> updatedDaysEnum = updatedWorkSchedule.getWorkScheduleDays().stream()
                .map(WorkScheduleDay::getDay).collect(Collectors.toList());
        assertThat(updatedWorkSchedule.getWorkScheduleTitle()).isEqualTo(workScheduleTitle);
        assertThat(updatedWorkSchedule.getIsActive()).isFalse();
        assertThat(updatedDaysEnum).isEqualTo(daysEnum);
    }

    @Test
    @DisplayName("근무일정 삭제 - 성공")
    void deleteWorkSchedule() throws Exception {
        WorkSchedule workSchedule = createWorkSchedule();

        ResultActions result = mockMvc.perform(delete(URL)
                .param("workScheduleId", String.valueOf(workSchedule.getIdWorkSchedule())));

        result.andExpect(status().isOk());
        List<WorkSchedule> workSchedules = workScheduleRepository.findAll();
        assertThat(workSchedules.size()).isZero();
    }

    @Test
    @Transactional
    @DisplayName("근무일정 상세조회 - 성공")
    void getWorkSchedule() throws Exception {
        WorkSchedule workSchedule = createWorkSchedule();

        ResultActions result = mockMvc.perform(get(URL)
                .param("workScheduleId", String.valueOf(workSchedule.getIdWorkSchedule())));

        List<Integer> days = workSchedule.getWorkScheduleDays().stream()
                        .map(workScheduleDay -> Day.fromDay(workScheduleDay.getDay())).collect(Collectors.toList());
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.workScheduleTitle").value(workSchedule.getWorkScheduleTitle()))
                .andExpect(jsonPath("$.data.days").value(days));
    }

    @Test
    @DisplayName("근무일정 전체 조회 - 성공")
    void getWorkSchedules() throws Exception {
        WorkSchedule workSchedule = createWorkSchedule();

        ResultActions result = mockMvc.perform(get(URL + "/list")
                .param("workplaceId", String.valueOf(workSchedule.getWorkplace().getIdWorkPlace())));

        System.out.println(result.andReturn().getResponse().getContentAsString());
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].workScheduleTitle").value(workSchedule.getWorkScheduleTitle()));
    }


    private WorkSchedule createWorkSchedule() {
        final Workplace workplace = workplaceRepository.findAll().get(0);
        final String workScheduleTitle = "오전일정";
        final LocalTime startTime = LocalTime.of(9, 0);
        final LocalTime endTime = LocalTime.of(13, 0);
        final List<Integer> days = List.of(1, 2, 3);

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
}