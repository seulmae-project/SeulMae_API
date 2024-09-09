package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.support.ServiceTestSupport;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.dto.response.UserProfileResponse;
import com.seulmae.seulmae.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class UserServiceTest extends ServiceTestSupport {
    @MockBean
    private SmsService smsService;


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


    @Test
    @DisplayName("회원가입을 한다")
    void createUser() {
        // GIVEN
        UserSignUpDto userSignUpDto = new UserSignUpDto(accountId, password, phoneNumber, name, isMale, birthday);

        // WHEN
        userService.createUser(userSignUpDto, file);

        // Then
        User user = userRepository.findAll().getFirst();
        assertThat(user.getAccountId()).isEqualTo(accountId);

    }

    @Test
    @DisplayName("회원가입시 휴대폰 인증 SMS를 보낸다.")
    void sendSMSCertificationWhenSignup() {
        // GIVEN
        String sendingType = "signUp";

        SmsSendingRequest smsSendingRequest = new SmsSendingRequest();
        smsSendingRequest.setSendingType(sendingType);
        smsSendingRequest.setPhoneNumber(phoneNumber);

        // SmsService의 sendSMS 메서드를 모의하여 실제로 실행되지 않도록 처리
        doNothing().when(smsService).sendSMS(anyString());

        // WHEN
        FindAuthResponse findAuthResponse = userService.sendSMSCertification(smsSendingRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).sendSMS(smsSendingRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("아이디 찾기 시, 휴대폰 인증 SMS를 보낸다.")
    void sendSMSCertificationWhenFindingId() {
        // GIVEN
        User user= mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        String sendingType = "findAccountId";
        SmsSendingRequest smsSendingRequest = new SmsSendingRequest();
        smsSendingRequest.setSendingType(sendingType);
        smsSendingRequest.setPhoneNumber(user.getPhoneNumber());

        doNothing().when(smsService).sendSMS(anyString());

        // WHEN
        FindAuthResponse findAuthResponse = userService.sendSMSCertification(smsSendingRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).sendSMS(smsSendingRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("비밀번호 찾기 시, 휴대폰 인증 SMS를 보낸다.")
    void sendSMSCertificationWhenFindingPw() {
        // GIVEN
        User user= mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        String sendingType = "findPassword";
        SmsSendingRequest smsSendingRequest = new SmsSendingRequest();
        smsSendingRequest.setSendingType(sendingType);
        smsSendingRequest.setPhoneNumber(user.getPhoneNumber());
        smsSendingRequest.setAccountId(user.getAccountId());

        doNothing().when(smsService).sendSMS(anyString());

        // WHEN
        FindAuthResponse findAuthResponse = userService.sendSMSCertification(smsSendingRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).sendSMS(smsSendingRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("회원가입시, SMS 코드를 통해 인증에 성공한다.")
    void confirmSMSCertificationWhenSignup() {
        // GIVEN
        String sendingType = "signUp";
        String authCode = "123456";

        SmsCertificationRequest smsCertificationRequest = new SmsCertificationRequest(sendingType, phoneNumber,authCode);

        doNothing().when(smsService).verifySMS(any());

        // WHEN
        FindAuthResponse findAuthResponse = userService.confirmSMSCertification(smsCertificationRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).verifySMS(smsCertificationRequest);
    }

    @Test
    @DisplayName("아이디 찾기 시, SMS 코드를 통해 인증에 성공한다.")
    void confirmSMSCertificationWhenFindingId() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        String sendingType = "findAccountId";
        String authCode = "123456";

        SmsCertificationRequest smsCertificationRequest = new SmsCertificationRequest(sendingType, user.getPhoneNumber(), authCode);

        doNothing().when(smsService).verifySMS(any());

        // WHEN
        FindAuthResponse findAuthResponse = userService.confirmSMSCertification(smsCertificationRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNotNull();
        verify(smsService, times(1)).verifySMS(smsCertificationRequest);
    }

    @Test
    @DisplayName("비밀번호 찾기 시, SMS 코드를 통해 인증에 성공한다.")
    void confirmSMSCertificationWhenFindingPw() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        String sendingType = "findPassword";
        String authCode = "123456";

        SmsCertificationRequest smsCertificationRequest = new SmsCertificationRequest(sendingType, phoneNumber,authCode);

        doNothing().when(smsService).verifySMS(any());

        // WHEN
        FindAuthResponse findAuthResponse = userService.confirmSMSCertification(smsCertificationRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).verifySMS(smsCertificationRequest);
    }

    @Test
    @DisplayName("소셜로 가입할 때 추가적인 프로필 정보를 저장한다")
    void updateAdditionalProfile() {
        // GIVEN
        String socialName = "소셜테스트이름";
        String socialBirthday = "19920913";
        boolean isSocialMale = true;

        String socialId = "socialId";
        SocialType socialType = SocialType.KAKAO;

        User user= mockSetUpUtil.creatSocialUser(accountId, password, socialId, socialType);
        OAuth2AdditionalDataRequest oAuth2AdditionalDataRequest = new OAuth2AdditionalDataRequest(socialName, isSocialMale, socialBirthday);

        // WHEN
        userService.updateAdditionalProfile(socialId, socialType, oAuth2AdditionalDataRequest, file);

        // THEN
        assertThat(user.getAuthorityRole()).isEqualTo(Role.USER);
        assertThat(user.getName()).isEqualTo(socialName);
    }

    @Test
    @DisplayName("본인의 프로필을 조회한다.")
    void getMyProfile() {
        // GIVEN
        User user= mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // WHEN
        UserProfileResponse userProfileResponse = userService.getMyProfile(user, request);

        // THEN
        assertThat(userProfileResponse.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("특정 유저의 프로필을 조회한다.")
    void getUserProfile() {
        // GIVEN
        User user= mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // WHEN
        UserProfileResponse userProfileResponse = userService.getUserProfile(user.getIdUser(), request);

        // THEN
        assertThat(userProfileResponse.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("유저 프로필을 수정한다.")
    void updateUser() throws AccessDeniedException {
        // GIVEN
        String newName = "테스트이름";
        User user= mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(newName);

        // WHEN
        userService.updateUser(user.getIdUser(), user, updateUserRequest, file);

        // Then
        assertThat(user.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("앱 탈퇴를 한다.")
    void deleteUser() throws AccessDeniedException {
        // GIVEN
        User user= mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        // WHEN
        userService.deleteUser(user.getIdUser(), user);

        // THEN
        assertThat(user.getIsDelUser()).isTrue();
    }


}