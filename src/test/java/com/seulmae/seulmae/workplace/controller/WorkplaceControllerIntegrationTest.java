package com.seulmae.seulmae.workplace.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.util.AuthenticationHelper;
import com.seulmae.seulmae.util.MockUser;
import com.seulmae.seulmae.util.UserUtil;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceModifyDto;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import com.seulmae.seulmae.workplace.service.WorkplaceService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkplaceControllerIntegrationTest {

    /** 스프링 MVC의 통합테스트를 위한 라이브러리, HTTP 요청을 모의로 생성하고 컨트롤러 메서드를 호출하여 응답을 검증
     *  실제 서블릿 컨테이너를 시작하지 않고도 Spring MVC 애플리케이션의 엔드포인트를 테스트할 수 있음 **/
    @Autowired
    protected MockMvc mockMvc;

    /** Spring 애플리케이션의 웹 애플리케이션 컨텍스트를 나타내는 인터페이스. 이 컨텍스트는 Spring MVC의 모든 구성 요소를 포함함 **/
    @Autowired
    private WebApplicationContext webApplicationContext;

    /** Jackson 라이브러리에서 제공하는 클래스로, Java 객체를 JSON으로 직렬화하거나 JSON을 Java 객체로 역직렬화하는 데 사용
     *  테스트에서 요청 본문을 JSON 형식으로 만들거나 응답 본문을 Java 객체로 변환하는 데 사용됨 **/
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private UserWorkplaceRepository userWorkplaceRepository;
    private String workplaceEndPoint = "/api/workplace/v1";


    /** JUnit 5에서 제공하는 어노테이션으로, 각 테스트 메서드가 실행되기 전에 특정 작업을 수행하기 위해 사용
     *  - 테스트 일관성 유지
     *  - 유지보수성 향상
     */
    @BeforeEach
    public void setUp() throws Exception {
        /** MockMvc 인스턴스를 초기화
         *  애플리케이션의 전체 컨텍스트를 사용하여 MockMvc를 설정 **/
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    @DisplayName("근무지 생성")
    public void shouldCreateWorkplaceSuccessfully() throws Exception {
        String addEndPoint = workplaceEndPoint + "/add";

        userUtil.createDefaultTestUserAndLogin("test1234", "qwer1234!", "01012344321", "이름");

        WorkplaceAddDto workplaceAddDto = createAddWorkplaceObject();

        /** writeValueAsString - Java 객체를 JSON 문자열로 직렬화하는 메서드**/
        String request = objectMapper.writeValueAsString(workplaceAddDto);

        /** 이미지 파일 생성 **/
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/testImage.png"));

        MockMultipartFile mockMultipartFile = new MockMultipartFile("workplaceAddDto", "workplaceAddDto", "application/json; charset=UTF-8", request.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile mockImageFile = new MockMultipartFile("multipartFileList", "testImage.png", "image/png", imageBytes);

        mockMvc.perform( /** 특정 HTTP 요청을 모의 실행 - 기본 POST 요청 **/
                multipart(addEndPoint)
                        .file(mockMultipartFile)
                        .file(mockImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA) /** 요청의 Content-Type 헤더 MULTIPART_FORM_DATA 설정 **/
                        .accept(MediaType.APPLICATION_JSON) /** 응답의 Content-Type application/json 설정 **/
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        List<UserWorkplace> all = userWorkplaceRepository.findAll();

        assertThat(all).isNotEmpty();
        assertWorkplaceStructure(all.get(0).getWorkplace());
    }

    @Test
    @Transactional
    @DisplayName("근무지 전체 리스트 조회")
    public void getAllListWorkplace() throws Exception {
        String getAllEndPoint = workplaceEndPoint + "/info/all";

        shouldCreateWorkplaceSuccessfully();

        /**
         *  ResultActions - 체이닝 방식
         *                  상태 코드나 응답 내용 등의 검증을 쉽게 수행할 수 있음
         *                  직관적이고 간결한 코드 작성
         *
         *  MvcResult - 실행 결과를 저장하는 객체, ResultActions보다 세밀한 조작을 위해 사용
         *              응답의 본문, 상태 코드, 헤더 등을 개별적으로 가져와서 사용할 수 있음
         *              상태 코드 외에도, 응답 데이터를 직접 분석하거나 처리할 때 유리
         * **/
        MvcResult result = mockMvc.perform(
                get(getAllEndPoint)
                        .param("keyword", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        /** JSON 응답에서 'data' 필드를 추출하여 Workplace 배열로 변환 **/
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        Workplace[] workplaceList = objectMapper.readValue(jsonNode.get("data").traverse(), Workplace[].class);

        if (workplaceList != null && workplaceList.length > 0) {
            assertWorkplaceStructure(workplaceList[0]);
        }
    }

    @Test
    @Transactional
    @DisplayName("특정 근무지 조회")
    public void getSpecificWorkplace() throws Exception {
        String getSpecificEndPoint = workplaceEndPoint + "/info";

        shouldCreateWorkplaceSuccessfully();
        Workplace workplace = workplaceRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(
                get(getSpecificEndPoint)
                        .param("workplaceId", String.valueOf(workplace.getIdWorkPlace()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        Workplace workplaceResponse = objectMapper.readValue(jsonNode.get("data").traverse(), Workplace.class);

        assertWorkplaceStructure(workplaceResponse);
    }

    @Test
    @Transactional
    @DisplayName("근무지 수정")
    public void modifyWorkplace() throws Exception{
        String modifyEndPoint = workplaceEndPoint + "/modify";

        shouldCreateWorkplaceSuccessfully();
        Workplace workplace = workplaceRepository.findAll().get(0);

        WorkplaceModifyDto modifyWorkplace = createModifyWorkplaceObject(workplace);
        String modifyRequest = objectMapper.writeValueAsString(modifyWorkplace);

        byte[] modifyImageBytes = Files.readAllBytes(Paths.get("src/test/resources/testImage2.jpg"));

        MockMultipartFile mockMultipartFile = new MockMultipartFile("workplaceModifyDto", "workplaceModifyDto", "application/json", modifyRequest.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile mockImageFile = new MockMultipartFile("multipartFileList", "testImage.png", "image/png", modifyImageBytes);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.PATCH, modifyEndPoint)
                                .file(mockMultipartFile)
                                .file(mockImageFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("근무지 삭제")
    public void deleteWorkplace() throws Exception {
        String deleteEndPoint = workplaceEndPoint + "/delete";

        shouldCreateWorkplaceSuccessfully();
        Workplace workplace = workplaceRepository.findAll().get(0);

        mockMvc.perform(
                delete(deleteEndPoint)
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
    @DisplayName("근무지명 중복 확인")
    public void checkWorkplaceNameDuplicate() throws Exception {
        String checkNameEndPoint = workplaceEndPoint + "/duplicate/name";

        String duplicateName = "duplicateName";

        mockMvc.perform(
                        get(checkNameEndPoint)
                                .param("workplaceName", duplicateName)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("유저별 가입 근무지 리스트")
    public void getJoinWorkplaceList() throws Exception{
        String joinWorkplaceEndPoint = workplaceEndPoint + "/info/join";

        shouldCreateWorkplaceSuccessfully();

        mockMvc.perform(
                        get(joinWorkplaceEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
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

    public WorkplaceModifyDto createModifyWorkplaceObject(Workplace workplace) throws Exception {
        return WorkplaceModifyDto.builder()
                .workplaceId(workplace.getIdWorkPlace())
                .workplaceName("modifyName")
                .workplaceTel("02-4321-4321")
                .mainAddress("수정메인주소")
                .subAddress("수정서브주소")
                .build();
    }

    /** 객체 구조 검증 메서드 **/
    private void assertWorkplaceStructure(Workplace workplace) {
        assertThat(workplace).isNotNull();
        assertThat(workplace.getWorkplaceName()).isEqualTo("testName");
        assertThat(workplace.getWorkplaceTel()).isEqualTo("02-1234-1234");
    }
}
