package com.seulmae.seulmae.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class WorkplaceUtil {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserUtil userUtil;

    private String workplaceEndPoint = "/api/workplace/v1";

    public MvcResult createWorkplace() throws Exception {
        String addEndPoint = workplaceEndPoint + "/add";

        userUtil.createDefaultTestUserAndLogin("test1234", "qwer1234!", "01012344321", "이름");

        WorkplaceAddDto workplaceAddDto = createAddWorkplaceObject();

        /** writeValueAsString - Java 객체를 JSON 문자열로 직렬화하는 메서드**/
        String request = objectMapper.writeValueAsString(workplaceAddDto);

        /** 이미지 파일 생성 **/
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/test/resources/testImage.png"));

        MockMultipartFile mockMultipartFile = new MockMultipartFile("workplaceAddDto", "workplaceAddDto", "application/json; charset=UTF-8", request.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile mockImageFile = new MockMultipartFile("multipartFileList", "testImage.png", "image/png", imageBytes);

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
                .andExpect(status().isCreated())
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
}
