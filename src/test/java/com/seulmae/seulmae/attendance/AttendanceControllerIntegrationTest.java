package com.seulmae.seulmae.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.attendance.dto.AttendanceApprovalDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestDto;
import com.seulmae.seulmae.attendanceRequestHistory.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.attendanceRequestHistory.repository.AttendanceRequestHistoryRepository;
import com.seulmae.seulmae.util.AttendanceUtil;
import com.seulmae.seulmae.util.UserUtil;
import com.seulmae.seulmae.util.WorkplaceUtil;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AttendanceControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private AttendanceRequestHistoryRepository attendanceRequestHistoryRepository;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private WorkplaceUtil workplaceUtil;

    @Autowired
    private AttendanceUtil attendanceUtil;

    private String attendanceEndPoint = "/api/attendance/v1";

    @BeforeEach
    public void setUp() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    @DisplayName("근무자 출/퇴근")
    public void testSendAttendanceRequest() throws Exception {
        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();

        AttendanceRequestDto attendanceRequestDto = createAttendanceObject(workplace, "2024-09-21", "2024-09-21T09:09:07.328938", "2024-09-21T15:02:34", 60000, 60000, BigDecimal.valueOf(6), 1);

        MvcResult mvcResult = attendanceUtil.sendAttendanceRequest(mockMvc, attendanceRequestDto);

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @Transactional
    @DisplayName("근무자 출/퇴근 승인")
    public void testSendAttendanceApproval() throws Exception {
        String endPoint = attendanceEndPoint + "/manager/approval";

        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        AttendanceRequestDto attendanceRequestDto = createAttendanceObject(workplace, "2024-09-21", "2024-09-21T08:49:07.328938", "2024-09-21T15:02:34.284727", 60000, 60000, BigDecimal.valueOf(6), 3);

        attendanceUtil.sendAttendanceRequest(mockMvc, attendanceRequestDto);

        AttendanceRequestHistory attendanceRequestHistory = attendanceRequestHistoryRepository.findAll().getFirst();

        AttendanceApprovalDto attendanceApprovalObject = createAttendanceApprovalObject(attendanceRequestHistory);
        String content = objectMapper.writeValueAsString(attendanceApprovalObject);

        mockMvc.perform(
                        post(endPoint)
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("근무자 출/퇴근 거절")
    public void testSendAttendanceRejection() throws Exception {
        String endPoint = attendanceEndPoint + "/manager/rejection";

        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        AttendanceRequestDto attendanceRequestDto = createAttendanceObject(workplace, "2024-09-21", "2024-09-21T08:49:07.328938", "2024-09-21T15:02:34.284727", 60000, 60000, BigDecimal.valueOf(6), 3);

        attendanceUtil.sendAttendanceRequest(mockMvc, attendanceRequestDto);

        AttendanceRequestHistory attendanceRequestHistory = attendanceRequestHistoryRepository.findAll().getFirst();

        mockMvc.perform(
                        post(endPoint)
                                .param("attendanceRequestHistoryId", String.valueOf(attendanceRequestHistory.getIdAttendanceRequestHistory()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("근무자 출/퇴근 요청 리스트")
    public void testGetAttendanceRequestList() throws Exception {
        String endPoint = attendanceEndPoint + "/request/list";

        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        AttendanceRequestDto attendanceRequestDto = createAttendanceObject(workplace, "2024-09-21", "2024-09-21T08:49:07.328938", "2024-09-21T15:02:34.284727", 60000, 60000, BigDecimal.valueOf(6), 3);

        attendanceUtil.sendAttendanceRequest(mockMvc, attendanceRequestDto);

        mockMvc.perform(
                        get(endPoint)
                                .param("workplaceId", String.valueOf(workplace.getIdWorkPlace()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("근무자 별도 근무 요청")
    public void testSendSeparateAttendanceRequest() throws Exception {
        String endPoint = attendanceEndPoint + "/separate";

        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        AttendanceRequestDto attendanceRequestDto = createAttendanceObject(workplace, "2024-09-21", "2024-09-21T10:09:07.328938", "2024-09-21T15:02:34", 60000, 60000, BigDecimal.valueOf(6), 3);
        String content = objectMapper.writeValueAsString(attendanceRequestDto);

        userUtil.loginTestUser("test12345");

        mockMvc.perform(
                        post(endPoint)
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("매니저 일별 근무자 요청 리스트")
    public void testGetDailyEmployeeAttendanceList() throws Exception {
        String endPoint = attendanceEndPoint + "/main/manager";

        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        AttendanceRequestDto attendanceRequestDto = createAttendanceObject(workplace, "2024-09-21", "2024-09-21T08:49:07.328938", "2024-09-21T15:02:34.284727", 60000, 60000, BigDecimal.valueOf(6), 3);

        attendanceUtil.sendAttendanceRequest(mockMvc, attendanceRequestDto);

        mockMvc.perform(
                        get(endPoint)
                                .param("workplace", String.valueOf(workplace.getIdWorkPlace()))
                                .param("localDate", String.valueOf("2024-09-21"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    public AttendanceRequestDto createAttendanceObject(Workplace workplace, String workDate, String workStartTime, String workEndTime, Integer confirmedWage, Integer unconfirmedWage, BigDecimal totalWorkTime, Integer day) {
        return AttendanceRequestDto.builder()
                .workplaceId(workplace.getIdWorkPlace())
                .workDate(LocalDate.parse(workDate))
                .workStartTime(LocalDateTime.parse(workStartTime))
                .workEndTime(LocalDateTime.parse(workEndTime))
                .confirmedWage(confirmedWage)
                .unconfirmedWage(unconfirmedWage)
                .totalWorkTime(totalWorkTime)
                .deliveryMessage("deliveryMessage")
                .day(day)
                .build();
    }

    public AttendanceApprovalDto createAttendanceApprovalObject(AttendanceRequestHistory attendanceRequestHistory) {
        return AttendanceApprovalDto.builder()
                .attendanceRequestHistoryId(attendanceRequestHistory.getIdAttendanceRequestHistory())
                .confirmedWage(60000)
                .changedWorkStartTime(LocalDateTime.parse("2024-09-21T08:49:07.328938"))
                .changedWorkEndTime(LocalDateTime.parse("2024-09-21T15:02:34.284727"))
                .build();
    }
}
