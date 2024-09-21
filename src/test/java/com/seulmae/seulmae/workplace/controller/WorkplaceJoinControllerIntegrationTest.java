package com.seulmae.seulmae.workplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.util.WorkplaceUtil;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkplaceJoinControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WorkplaceUtil workplaceUtil;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private WorkplaceApproveRepository workplaceApproveRepository;

    private String workplaceJoinEndPoint = "/api/workplace/join/v1";

    @BeforeEach
    public void setUp() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    @DisplayName("근무지 입장 요청")
    public void testSendJoinRequest() throws Exception {
        MvcResult mvcResult = workplaceUtil.joinWorkplace(mockMvc);

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @Transactional
    @DisplayName("근무지 입장 수락")
    public void testSendJoinApproval() throws Exception {
        MvcResult mvcResult = workplaceUtil.joinApproval(mockMvc);

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @Transactional
    @DisplayName("근무지 입장 거절")
    public void testSendJoinRejection() throws Exception {
        String endPoint = workplaceJoinEndPoint + "/rejection";

        workplaceUtil.joinWorkplace(mockMvc);

        Long workplaceApproveId = workplaceApproveRepository.findAll().getFirst().getIdWorkPlaceApprove();

        mockMvc.perform(
                        post(endPoint)
                                .param("workplaceApproveId", String.valueOf(workplaceApproveId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("근무지 입장 요청 리스트")
    public void testGetWorkplaceRequestList() throws Exception {
        String endPoint = workplaceJoinEndPoint + "/request/list";

        workplaceUtil.joinWorkplace(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();

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
}

