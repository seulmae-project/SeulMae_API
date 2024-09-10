package com.seulmae.seulmae.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seulmae.seulmae.global.support.ControllerUnitTestSupport;
import com.seulmae.seulmae.user.dto.request.UpdateUserRequest;
import com.seulmae.seulmae.user.dto.request.UserSignUpDto;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.util.MockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 목적
 * 1. HTTP 요청과 응답의 흐름 집중 검증(즉, 사용자의 요청이 올바르게 처리되고 있는지 확인)
 * 2. 유효성 검증이 제대로 이루어지는지 확인
 * 3. 비즈니스 로직이 변경되거나 API가 확장되어도 기존 기능이 동일하게 동작하는지 검증
 */


public class UserControllerUnitTest extends ControllerUnitTestSupport {

    /**
     * 공통적인 설정
     */
    final String accountId = "test1234";
    final String password = "qwer1234!";
    final String phoneNumber = "01012341234";
    final String name = "테스트이름";
    final String birthday = "19930815";
    final boolean isMale = false;
    final MultipartFile file = null;

    private final String URL = "/api/users";

    @Test
    @DisplayName("회원가입 한다")
    void signUp() throws Exception {
        // GIVEN
        UserSignUpDto request = new UserSignUpDto(accountId, password, phoneNumber, name, isMale, birthday);
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile userSignUpDtoPart = new MockMultipartFile("userSignUpDto", "userSignUpDto", "application/json; charset=UTF-8", requestBody.getBytes(StandardCharsets.UTF_8));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.multipart(URL)
                        .file(userSignUpDtoPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

//    @Test
//    @DisplayName("프로필을 수정한다")
//    @MockUser()
//    void updateProfile() throws Exception {
//        // GIVEN
//        Long id = 1L;
//        String changingName = "newName";
//        UpdateUserRequest request = new UpdateUserRequest(changingName);
//        String requestBody = objectMapper.writeValueAsString(request);
//        MockMultipartFile updateUserRequestPart = new MockMultipartFile("updateUserRequest", "updateUserRequest", "application/json", requestBody.getBytes());
//        MockMultipartFile filePart = new MockMultipartFile("file", "profile.png", "image/png", "test image".getBytes());
//
//        doNothing().when(userService).updateUser(anyLong(), any(User.class), any(UpdateUserRequest.class), any(MultipartFile.class));
//
//        // WHEN & THEN
//        mockMvc.perform(multipart(URL)
//                        .file(updateUserRequestPart)
//                        .file(filePart)
//                        .param("id", String.valueOf(id))
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .with(csrf())
//                        .with(_request -> {
//                            _request.setMethod("PUT");
//                            return _request;
//                        })
//                )
//                .andDo(print())
//                .andExpect(status().isOk());
//    }


}
