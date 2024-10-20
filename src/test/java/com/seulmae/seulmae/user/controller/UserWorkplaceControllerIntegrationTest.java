package com.seulmae.seulmae.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.dto.request.ManagerDelegationRequest;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserWorkplaceControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private UserWorkplaceRepository userWorkplaceRepository;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private WorkplaceUtil workplaceUtil;

    private String userWorkplaceEndPoint = "/api/workplace/user/v1";

    @BeforeEach
    public void setUp() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    @DisplayName("근무지 유저 상세조회")
    public void testGetUserInfoWithUserWorkplace() throws Exception {
        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        User user = userRepository.findByAccountId("testAccountId2").orElseThrow(() -> new NullPointerException("존재하지 않는 사용자 계정입니다."));

        UserWorkplace userWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(user, workplace).orElseThrow(() -> new NullPointerException("User 또는 Workplace가 존재하지 않습니다."));

        mockMvc.perform(
                        get(userWorkplaceEndPoint)
                                .param("userWorkplaceId", String.valueOf(userWorkplace.getIdUserWorkplace()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("매니저 권한 위임")
    public void testDelegateManagerAuthority() throws Exception {
        String endPoint = userWorkplaceEndPoint + "/manager/delegate";

        workplaceUtil.joinApproval(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        User user = userRepository.findByAccountId("testAccountId2").orElseThrow(() -> new NullPointerException("존재하지 않는 사용자 계정입니다."));

        ManagerDelegationRequest managerDelegationObject = createManagerDelegationObject(user, workplace);
        String content = objectMapper.writeValueAsString(managerDelegationObject);

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
    @DisplayName("근무지 유저 삭제")
    public void testWithdrawWorkplace() throws Exception {
        workplaceUtil.joinApproval(mockMvc);

        userUtil.loginTestUser("testAccountId2");

        Workplace workplace = workplaceRepository.findAll().getFirst();

        mockMvc.perform(
                        delete(userWorkplaceEndPoint)
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
    @DisplayName("근무지에 포함된 모든 유저 리스트")
    public void testGetAllUserFromWorkplace() throws Exception {
        String endPoint = userWorkplaceEndPoint + "/list";

        workplaceUtil.joinApproval(mockMvc);

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

    public ManagerDelegationRequest createManagerDelegationObject(User user, Workplace workplace) {
        return ManagerDelegationRequest.builder()
                .userId(user.getIdUser())
                .workplaceId(workplace.getIdWorkPlace())
                .build();
    }
}
