package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.exception.InvalidAccountIdException;
import com.seulmae.seulmae.global.exception.InvalidPasswordException;
import com.seulmae.seulmae.global.support.ControllerUnitTestSupport;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.dto.response.UserProfileResponse;
import com.seulmae.seulmae.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 목적
 * 1. HTTP 요청과 응답의 흐름 집중 검증(즉, 사용자의 요청이 올바르게 처리되고 있는지 확인)
 * 2. 유효성 검증이 제대로 이루어지는지 확인
 * 3. 비즈니스 로직이 변경되거나 API가 확장되어도 기존 기능이 동일하게 동작하는지 검증
 */

@DisplayName("UserController 단위 테스트")
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

    @Test
    @DisplayName("조건을 충족하지 않아서 회원가입에 실패한다")
    void signUpFailNotMeetCondition() throws Exception {
        UserSignUpDto request = new UserSignUpDto(null, password, phoneNumber, name, isMale, birthday);
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile userSignUpDtoPart = new MockMultipartFile("userSignUpDto", "userSignUpDto", "application/json; charset=UTF-8", requestBody.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile filePart = new MockMultipartFile("file", "profile.png", "image/png", "test image".getBytes());
        doThrow(new IllegalArgumentException("아이디와 비밀번호는 필수 입력 정보입니다."))
                .when(userService).createUser(any(UserSignUpDto.class), any(MultipartFile.class));


        mockMvc.perform(MockMvcRequestBuilders.multipart(URL)
                        .file(userSignUpDtoPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).createUser(any(UserSignUpDto.class), any(MultipartFile.class));

    }

    @Test
    @DisplayName("조건을 충족하지 않아서 회원가입에 실패한다 (파일 없음)")
    void signUpFailNotMeetConditionWithoutFile() throws Exception {
        // given
        UserSignUpDto request = new UserSignUpDto(null, password, phoneNumber, name, isMale, birthday);
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile userSignUpDtoPart = new MockMultipartFile("userSignUpDto", "userSignUpDto", "application/json; charset=UTF-8", requestBody.getBytes(StandardCharsets.UTF_8));

        // 서비스 호출 시 예외 발생 설정
        doThrow(new IllegalArgumentException("아이디와 비밀번호는 필수 입력 정보입니다."))
                .when(userService).createUser(any(UserSignUpDto.class), isNull());

        // when
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
                        .file(userSignUpDtoPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // then
        verify(userService, times(1)).createUser(any(UserSignUpDto.class), isNull()); // 파일이 없으므로 null로 검증
    }

    @Test
    @DisplayName("비밀번호가 타당하지않아 회원가입에 실패한다")
    void signUpFailInvalidPw() throws Exception {
        UserSignUpDto request = new UserSignUpDto();
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile userSignUpDtoPart = new MockMultipartFile("userSignUpDto", "userSignUpDto", "application/json; charset=UTF-8", requestBody.getBytes(StandardCharsets.UTF_8));
        String errorDesc = "비밀번호로 영문, 숫자, 특수문자 포함 8자 이상을 입력해주세요.";


        doThrow(new InvalidPasswordException(errorDesc))
                .when(userService).createUser(any(UserSignUpDto.class), isNull());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
                        .file(userSignUpDtoPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDescription").value(errorDesc));
    }


    @Test
    @DisplayName("비밀번호가 타당하지않아 회원가입에 실패한다")
    void signUpFailInvalidId() throws Exception {
        UserSignUpDto request = new UserSignUpDto();
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile userSignUpDtoPart = new MockMultipartFile("userSignUpDto", "userSignUpDto", "application/json; charset=UTF-8", requestBody.getBytes(StandardCharsets.UTF_8));
        String errorDesc = "아이디로 영문 또는 숫자 5자 이상을 입력해주세요.";


        doThrow(new InvalidAccountIdException(errorDesc))
                .when(userService).createUser(any(UserSignUpDto.class), isNull());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
                        .file(userSignUpDtoPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDescription").value(errorDesc));
    }

    @Test
    @DisplayName("프로필을 수정한다")
    void updateProfile() throws Exception {
        // GIVEN
        Long id = 1L;
        User mockUser = User.builder().build();
        mockUser.setIdUser(id);
        when(authenticationHelper.getCurrentUser()).thenReturn(mockUser);

        String changingName = "newName";
        UpdateUserRequest request = new UpdateUserRequest(changingName);
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile updateUserRequestPart = new MockMultipartFile("updateUserRequest", "updateUserRequest", "application/json", requestBody.getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "profile.png", "image/png", "test image".getBytes());

        // WHEN & THEN
        mockMvc.perform(multipart(URL)
                        .file(updateUserRequestPart)
                        .file(filePart)
                        .param("id", String.valueOf(mockUser.getIdUser()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(user(mockUser))
                        .with(csrf())
                        .with(_request -> {
                            _request.setMethod("PUT");
                            return _request;
                        })
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("권한이 없어 프로필 수정 실패한다")
    void updateProfileFailNoAuth() throws Exception {
        // GIVEN
        Long id = 1L;
        User mockUser = User.builder().build();
        mockUser.setIdUser(id);
        when(authenticationHelper.getCurrentUser()).thenReturn(mockUser);

        String changingName = "newName";
        UpdateUserRequest request = new UpdateUserRequest(changingName);
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile updateUserRequestPart = new MockMultipartFile("updateUserRequest", "updateUserRequest", "application/json", requestBody.getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "profile.png", "image/png", "test image".getBytes());

        String errorDesc = "프로필을 수정할 권한이 없습니다.";

        doThrow(new AccessDeniedException(errorDesc))
                .when(userService).updateUser(anyLong(), any(User.class), any(UpdateUserRequest.class), any(MultipartFile.class));

        // WHEN & THEN
        mockMvc.perform(multipart(URL)
                        .file(updateUserRequestPart)
                        .file(filePart)
                        .param("id", String.valueOf(mockUser.getIdUser()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(user(mockUser))
                        .with(csrf())
                        .with(_request -> {
                            _request.setMethod("PUT");
                            return _request;
                        })
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorDescription").value(errorDesc));

    }

    @Test
    @DisplayName("앱을 탈퇴한다")
    void deleteUser() throws Exception {
        Long id = 1L;
        User mockUser = User.builder().build();
        mockUser.setIdUser(id);
        when(authenticationHelper.getCurrentUser()).thenReturn(mockUser);

        mockMvc.perform(delete(URL)
                        .param("id", String.valueOf(mockUser.getIdUser()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(user(mockUser))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("권한이 없어 앱 탈퇴 실패한다")
    void deleteUserFailNoAuth() throws Exception {
        Long id = 1L;
        User mockUser = User.builder().build();
        mockUser.setIdUser(id);
        when(authenticationHelper.getCurrentUser()).thenReturn(mockUser);

        String errorDesc = "앱을 탈퇴할 권한이 없습니다.";

        doThrow(new AccessDeniedException(errorDesc))
                .when(userService).deleteUser(anyLong(), any(User.class));

        mockMvc.perform(delete(URL)
                        .param("id", String.valueOf(mockUser.getIdUser()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(user(mockUser))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorDescription").value(errorDesc));

    }

    @Test
    @DisplayName("소셜 아이디로 가입할 때, 추가적인 프로필 정보를 입력한다")
    void updateAdditionalProfile() throws Exception {

        // GIVEN
        User mockUser = User.builder()
                .socialId("socialId")
                .socialType(SocialType.KAKAO)
                .authorityRole(Role.GUEST)
                .build();

        when(authenticationHelper.getCurrentUser()).thenReturn(mockUser);

        String name = "소셜이름";
        Boolean isMale = true;
        String birthday = "20000121";
        OAuth2AdditionalDataRequest request = new OAuth2AdditionalDataRequest(name, isMale, birthday);
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile oAuth2AdditionalDataRequestPart = new MockMultipartFile("oAuth2AdditionalDataRequest", "oAuth2AdditionalDataRequest", "application/json", requestBody.getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "profile.png", "image/jpg", "test image".getBytes());


        // WHEN & THEN
        mockMvc.perform(multipart(URL + "/extra-profile")
                        .file(oAuth2AdditionalDataRequestPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(user(mockUser))
                        .with(csrf())
                        .with(_request -> {
                            _request.setMethod("PUT");
                            return _request;
                        })
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 ID로 프로필 정보를 확인한다")
    @WithMockUser
    void getUserProfile() throws Exception {
        // GIVEN
        User mockUser = User.builder().idUser(1L).build();
        when(authenticationHelper.getCurrentUser()).thenReturn(mockUser);
        UserProfileResponse result = new UserProfileResponse(null, null, null, List.of());
        when(userService.getUserProfile(anyLong(), any(HttpServletRequest.class))).thenReturn(result);


        // WHEN & THEN
        mockMvc.perform(get(URL)
                        .param("id", String.valueOf(mockUser.getIdUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());

    }

    @Test
    @DisplayName("내 프로필 정보를 확인한다")
    void getMyProfile() throws Exception {
        // GIVEN
        User mockUser = User.builder().build();
        UserProfileResponse result = new UserProfileResponse(null, null, null, List.of());
        when(userService.getMyProfile(any(User.class), any())).thenReturn(result);

        // WHEN & THEN
        mockMvc.perform(get(URL + "/my-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(user(mockUser))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());

    }

    @Test
    @WithMockUser
    @DisplayName("SMS로 인증 코드를 보낸다")
    void sendSMS() throws Exception {
        // GIVEN
        SmsSendingRequest request = new SmsSendingRequest("signup", "01012341234");
        String requestBody = objectMapper.writeValueAsString(request);

        FindAuthResponse result = new FindAuthResponse(true);
        when(userService.sendSMSCertification(any(SmsSendingRequest.class))).thenReturn(result);

        System.out.println("result = " + result);

        // WHEN & THEN
        mockMvc.perform(post(URL + "/sms-certification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("SMS로 인증 코드를 확인한다")
    void verifySMS() throws Exception {
        // GIVEN
        SmsCertificationRequest request = new SmsCertificationRequest("findAccountId", "01012341234", "010111");
        String requestBody = objectMapper.writeValueAsString(request);

        FindAuthResponse result = new FindAuthResponse(true, "accountId123");
        when(userService.confirmSMSCertification(any(SmsCertificationRequest.class))).thenReturn(result);

        // WHEN & THEN
        mockMvc.perform(post(URL + "/sms-certification/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("아이디 중복 여부를 확인한다.")
    void checkAccountId() throws Exception {
        String newAccountId = "newAccount1234";
        CheckAccountIdRequest request = new CheckAccountIdRequest(newAccountId);
        String requestBody = objectMapper.writeValueAsString(request);

        when(userService.isDuplicatedAccountId(request.getAccountId())).thenReturn(true);

        // WHEN & THEN
        mockMvc.perform(post(URL + "/id/duplication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    @WithMockUser
    void changePassword() throws Exception {
        String existAccount = "account1234";
        String newPassword = "abc12342!";
        ChangePasswordRequest request = new ChangePasswordRequest(existAccount, newPassword);
        String requestBody = objectMapper.writeValueAsString(request);

        doNothing().when(userService).changePassword(any(ChangePasswordRequest.class));

        mockMvc.perform(put(URL + "/pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("휴대폰 번호를 변경한다")
    void changePhoneNumber() throws Exception {
        User mockUser = User.builder().idUser(1L).build();
        when(authenticationHelper.getCurrentUser()).thenReturn(mockUser);

        String newPhoneNumber = "01012342222";
        ChangePhoneNumberRequest request = new ChangePhoneNumberRequest(newPhoneNumber);
        String requestBody = objectMapper.writeValueAsString(request);
        doNothing().when(userService).changePhoneNumber(anyLong(), any(ChangePhoneNumberRequest.class), any(User.class));

        mockMvc.perform(put(URL + "/phone")
                        .param("id", String.valueOf(mockUser.getIdUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(user(mockUser))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}
