package com.seulmae.seulmae.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.workplace.dto.JoinApprovalDto;
import com.seulmae.seulmae.workplace.dto.WorkScheduleAddDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceJoinDto;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class WorkplaceUtil {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private WorkplaceApproveRepository workplaceApproveRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;
    private String workplaceEndPoint = "/api/workplace/v1";

    private String workplaceJoinEndPoint = "/api/workplace/join/v1";


    public MvcResult createWorkplace(MockMvc mockMvc) throws Exception {
        String addEndPoint = workplaceEndPoint + "/add";

        userUtil.createDefaultTestUserAndLogin("testAccountId1", "qwer1234!", "01012344321", "이름");

        WorkplaceAddDto workplaceAddDto = createAddWorkplaceObject();

        /** writeValueAsString - Java 객체를 JSON 문자열로 직렬화하는 메서드**/
        String request = objectMapper.writeValueAsString(workplaceAddDto);

        /** 이미지 파일 생성 **/
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/testImage.png"));

        MockMultipartFile mockMultipartFile = new MockMultipartFile("workplaceAddDto", "workplaceAddDto", "application/json; charset=UTF-8", request.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile mockImageFile = new MockMultipartFile("multipartFileList", "testImage.png", "image/png", imageBytes);

        /** 테스트 환경에서만 MockMvc 주입 가능. 때문에 인자로 전달 **/
        return mockMvc.perform( /** 특정 HTTP 요청을 모의 실행 - 기본 POST 요청 **/
                        multipart(addEndPoint)
                                .file(mockMultipartFile)
                                .file(mockImageFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA) /** 요청의 Content-Type 헤더 MULTIPART_FORM_DATA 설정 **/
                                .accept(MediaType.APPLICATION_JSON) /** 응답의 Content-Type application/json 설정 **/
                                .with(SecurityMockMvcRequestPostProcessors.authentication(SecurityContextHolder.getContext().getAuthentication()))
                        /**     authentication 설정 이유
                         *      Spring Security는 MockMvc 요청을 보낼 때 별도의 Security 설정을 사용하기 때문에, SecurityContext에 설정한 사용자가 실제로 해당 요청에 적용되지 않음
                         *      이때 @SpringBootTest와 @AutoConfigureMockMvc 설정이 포함된 클래스에서는 Spring Security의 SecurityContext와 MockMvc가 자동으로 통합되기 때문에, SecurityContextHolder에 설정한 인증 정보가 MockMvc 요청에 자동으로 적용됨
                         *      WorkplaceUtil 클래스는 해당 설정이 없기 때문에 UserUtil 클래스에서 SecurityContext에 등록된 인증 정보가 MockMvc로 전달되지 않음
                         *      때문에 RequestPostProcessor 를 사용하여 현재 SecurityContextHolder 에 설정된 인증 정보를 MockMvc 요청에 적용
                         */
                )
                .andDo(print())
                .andReturn();
    }

    public MvcResult joinWorkplace(MockMvc mockMvc) throws Exception {
        String endPoint = workplaceJoinEndPoint + "/request";

        createWorkplace(mockMvc);

        userUtil.createDefaultTestUserAndLogin("testAccountId2", "qwer1234!", "01043211234", "이름");

        List<Workplace> workplaceList = workplaceRepository.findAll();
        Workplace workplace = workplaceList.getFirst();

        WorkplaceJoinDto workplaceJoinDto = createWorkplaceJoinObject(workplace);
        String content = objectMapper.writeValueAsString(workplaceJoinDto);

        return mockMvc.perform(
                        post(endPoint)
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
    }

    public MvcResult joinApproval(MockMvc mockMvc) throws Exception {
        String endPoint = workplaceJoinEndPoint + "/approval";

        joinWorkplace(mockMvc);

        Workplace workplace = workplaceRepository.findAll().getFirst();
        Long workplaceApproveId = workplaceApproveRepository.findAll().getFirst().getIdWorkPlaceApprove();

        testAddWorkSchedule(workplace, mockMvc);

        Long idWorkSchedule = workScheduleRepository.findAll().getFirst().getIdWorkSchedule();

        JoinApprovalDto joinApprovalDto = new JoinApprovalDto(idWorkSchedule, 25, 10000, "memo");
        String contentJoinApprovalDto = objectMapper.writeValueAsString(joinApprovalDto);

        return mockMvc.perform(
                        post(endPoint)
                                .param("workplaceApproveId", String.valueOf(workplaceApproveId))
                                .content(contentJoinApprovalDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
    }
    public WorkplaceAddDto createAddWorkplaceObject() throws Exception {

        return WorkplaceAddDto.builder()
                .workplaceName("testName")
                .workplaceTel("02-1234-1234")
                .mainAddress("메인주소")
                .subAddress("서브주소")
                .build();
    }

    public WorkplaceJoinDto createWorkplaceJoinObject(Workplace workplace){
        return WorkplaceJoinDto.builder()
                .workplaceId(workplace.getIdWorkPlace())
                .build();
    }

    public void testAddWorkSchedule(Workplace workplace, MockMvc mockMvc) throws Exception {
        String endPoint = "/api/schedule/v1";

        WorkScheduleAddDto workScheduleAddDto = new WorkScheduleAddDto(workplace.getIdWorkPlace(), "평일오전", LocalTime.of(9, 0), LocalTime.of(15, 0), List.of(1, 2));
        String request = objectMapper.writeValueAsString(workScheduleAddDto);

        userUtil.loginTestUser("testAccountId1");

        mockMvc.perform(
                        post(endPoint)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }
}
