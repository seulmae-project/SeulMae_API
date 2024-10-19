package com.seulmae.seulmae.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Component
public class AttendanceUtil {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserUtil userUtil;

    private String attendanceEndPoint = "/api/attendance/v1";

    public MvcResult sendAttendanceRequest(MockMvc mockMvc, AttendanceRequestDto attendanceRequestDto) throws Exception {
        String endPoint = attendanceEndPoint + "/finish";

        userUtil.loginTestUser("testAccountId2");

        String content = objectMapper.writeValueAsString(attendanceRequestDto);

        return mockMvc.perform(
                        post(endPoint)
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
    }
}
