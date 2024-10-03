package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.exception.InvalidAccountIdException;
import com.seulmae.seulmae.global.exception.InvalidPasswordException;
import com.seulmae.seulmae.global.exception.MatchPasswordException;
import com.seulmae.seulmae.global.support.ServiceTestSupport;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.AccountDuplicatedResponse;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.dto.response.UserProfileResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.enums.Role;
import com.seulmae.seulmae.user.enums.SmsSendingType;
import com.seulmae.seulmae.user.enums.SocialType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;



@Transactional
class UserServiceTest extends ServiceTestSupport {
    @MockBean
    private SmsService smsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


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
    @DisplayName("아이디가 중복되어 회원가입에 실패한다")
    void createUserFailWithDuplicatedId() {
        // GIVEN
        User beforeUser = mockSetUpUtil.createUser(accountId, password, "01011111111", name, birthday, isMale);
        UserSignUpDto userSignUpDto = new UserSignUpDto(accountId, password, phoneNumber, name, isMale, birthday);

        // WHEN & Then
        assertThatThrownBy(() -> userService.createUser(userSignUpDto, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 아이디입니다.");

    }

    @Test
    @DisplayName("휴대폰번호와 이름이 일치하는 계정이 있어 회원가입에 실패한다")
    void createUserFailWithDuplicatedPhoneNumberAndName() {
        // GIVEN
        User beforeUser = mockSetUpUtil.createUser("another1234", password, phoneNumber, name, birthday, isMale);
        UserSignUpDto userSignUpDto = new UserSignUpDto(accountId, password, phoneNumber, name, isMale, birthday);

        // WHEN & Then
        assertThatThrownBy(() -> userService.createUser(userSignUpDto, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가입한 계정이 존재합니다.");

    }

    @Test
    @DisplayName("비빌번호가 형식에 맞지 않아 회원가입에 실패한다")
    void createUserFailBecauseOfUnValidatedPassword() {
        // GIVEN
        UserSignUpDto userSignUpDto = new UserSignUpDto(accountId, "1111", phoneNumber, name, isMale, birthday);

        // WHEN & Then
        assertThatThrownBy(() -> userService.createUser(userSignUpDto, file))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("비밀번호로 영문, 숫자, 특수문자 포함 8자 이상을 입력해주세요.");
    }

    @Test
    @DisplayName("아이디가 형식에 맞지 않아 회원가입에 실패한다")
    void createUserFailBecauseOfUnValidatedId() {
        // GIVEN
        UserSignUpDto userSignUpDto = new UserSignUpDto("qwe1", password, phoneNumber, name, isMale, birthday);

        // WHEN & Then
        assertThatThrownBy(() -> userService.createUser(userSignUpDto, file))
                .isInstanceOf(InvalidAccountIdException.class)
                .hasMessage("아이디로 영문 또는 숫자 5자 이상을 입력해주세요.");
    }

    @Test
    @DisplayName("회원가입시 휴대폰 인증 SMS를 보낸다")
    void sendSMSCertificationWhenSignup() {
        // GIVEN
        SmsSendingRequest smsSendingRequest = new SmsSendingRequest(SmsSendingType.SIGNUP, name, phoneNumber);

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
    @DisplayName("회원가입시 휴대폰번호와 이름이 일치하는 계정이 있어 sms 보내기에 실패한다")
    void sendSMSCertificationFailWhenSignup() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        SmsSendingRequest smsSendingRequest = new SmsSendingRequest(SmsSendingType.SIGNUP, name, phoneNumber);

        // SmsService의 sendSMS 메서드를 모의하여 실제로 실행되지 않도록 처리
        doNothing().when(smsService).sendSMS(anyString());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.sendSMSCertification(smsSendingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가입한 계정이 존재합니다.");

    }

    @Test
    @DisplayName("아이디 찾기 시, 휴대폰 인증 SMS를 보낸다")
    void sendSMSCertificationWhenFindingId() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        SmsSendingRequest smsSendingRequest = new SmsSendingRequest(SmsSendingType.FIND_ACCOUNT_ID, user.getName(), user.getPhoneNumber());
        doNothing().when(smsService).sendSMS(anyString());

        // WHEN
        FindAuthResponse findAuthResponse = userService.sendSMSCertification(smsSendingRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).sendSMS(smsSendingRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("아이디 찾기 시, 계정이 존재하지 않아서 SMS 보내기에 실패한다")
    void sendSMSCertificationFailWhenFindingId() {
        // GIVEN
        SmsSendingRequest smsSendingRequest = new SmsSendingRequest(SmsSendingType.FIND_ACCOUNT_ID, name, phoneNumber);

        doNothing().when(smsService).sendSMS(anyString());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.sendSMSCertification(smsSendingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 이름과 휴대폰 번호와 일치하는 계정이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호 재설정 시, 휴대폰 인증 SMS를 보낸다")
    void sendSMSCertificationWhenChangingPw() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        SmsSendingRequest smsSendingRequest = new SmsSendingRequest(SmsSendingType.CHANGE_PW, user.getName(),user.getPhoneNumber());

        doNothing().when(smsService).sendSMS(anyString());

        // WHEN
        FindAuthResponse findAuthResponse = userService.sendSMSCertification(smsSendingRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).sendSMS(smsSendingRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("비밀번호 재설정 시, 휴대폰 번호와 이름 매칭되지 않아서 sms보내기에 실패한다")
    void sendSMSCertificationFailWhenChangingPw() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, "anotherpw1234!", phoneNumber, name, birthday, isMale);

        SmsSendingRequest smsSendingRequest = new SmsSendingRequest(SmsSendingType.CHANGE_PW, name,password);
        doNothing().when(smsService).sendSMS(anyString());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.sendSMSCertification(smsSendingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 이름과 휴대폰 번호와 일치하는 계정이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("잘못된 sendingType을 보내서 sms보내기에 실패한다")
    @Disabled
    void sendSMSCertificationFail() {
        // GIVEN
        String sendingType = "wrongSendingType";
        SmsSendingRequest smsSendingRequest = new SmsSendingRequest(null, name, password);

        doNothing().when(smsService).sendSMS(anyString());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.sendSMSCertification(smsSendingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(smsSendingRequest.getSendingType() + " 타입은 존재하지 않는 타입입니다.");
    }

    @Test
    @DisplayName("회원가입시, SMS 코드를 통해 인증에 성공한다")
    void confirmSMSCertificationWhenSignup() {
        // GIVEN
        String authCode = "123456";

        SmsCertificationRequest smsCertificationRequest = new SmsCertificationRequest(SmsSendingType.SIGNUP, phoneNumber, authCode);

        doNothing().when(smsService).verifySMS(any());

        // WHEN
        FindAuthResponse findAuthResponse = userService.confirmSMSCertification(smsCertificationRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).verifySMS(smsCertificationRequest);
    }

    @Test
    @DisplayName("아이디 찾기 시, SMS 코드를 통해 인증에 성공한다")
    void confirmSMSCertificationWhenFindingId() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        String authCode = "123456";

        SmsCertificationRequest smsCertificationRequest = new SmsCertificationRequest(SmsSendingType.FIND_ACCOUNT_ID, user.getPhoneNumber(), authCode);

        doNothing().when(smsService).verifySMS(any());

        // WHEN
        FindAuthResponse findAuthResponse = userService.confirmSMSCertification(smsCertificationRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNotNull();
        verify(smsService, times(1)).verifySMS(smsCertificationRequest);
    }

    @Test
    @DisplayName("비밀번호 재설정 시, SMS 코드를 통해 인증에 성공한다")
    void confirmSMSCertificationWhenFindingPw() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        String authCode = "123456";

        SmsCertificationRequest smsCertificationRequest = new SmsCertificationRequest(SmsSendingType.CHANGE_PW, phoneNumber, authCode);

        doNothing().when(smsService).verifySMS(any());

        // WHEN
        FindAuthResponse findAuthResponse = userService.confirmSMSCertification(smsCertificationRequest);

        // THEN
        assertThat(findAuthResponse.getIsSuccess()).isTrue();
        assertThat(findAuthResponse.getAccountId()).isNull();
        verify(smsService, times(1)).verifySMS(smsCertificationRequest);
    }

    @Test
    @DisplayName("잘못된 sendingType을 보내서, SMS 인증 실패한다")
    @Disabled
    void confirmSMSCertificationFail() {
        // GIVEN
        String authCode = "123456";

        SmsCertificationRequest smsCertificationRequest = new SmsCertificationRequest(null, phoneNumber, authCode);

        doNothing().when(smsService).verifySMS(any());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.confirmSMSCertification(smsCertificationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(smsCertificationRequest.getSendingType() + " 타입은 존재하지 않는 타입입니다.");
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

        User user = mockSetUpUtil.creatSocialUser(accountId, password, socialId, socialType);
        OAuth2AdditionalDataRequest oAuth2AdditionalDataRequest = new OAuth2AdditionalDataRequest(socialName, isSocialMale, socialBirthday);

        // WHEN
        userService.updateAdditionalProfile(socialId, socialType, oAuth2AdditionalDataRequest, file);

        // THEN
        assertThat(user.getAuthorityRole()).isEqualTo(Role.USER);
        assertThat(user.getName()).isEqualTo(socialName);
    }

    @Test
    @DisplayName("본인의 프로필을 조회한다")
    void getUserProfile() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // WHEN
        UserProfileResponse userProfileResponse = userService.getUserProfile(user, request);

        // THEN
        assertThat(userProfileResponse.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("유저 프로필을 수정한다")
    void updateUser() throws AccessDeniedException {
        // GIVEN
        String newName = "테스트이름";
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(newName);

        // WHEN
        userService.updateUser(user, updateUserRequest, file);

        // Then
        assertThat(user.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("수정 권한이 없는 유저가 프로필 수정에 실패한다")
    @Disabled
    void updateUserFail() throws AccessDeniedException {
        // GIVEN
        String newName = "테스트이름";
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        User anotherUser = mockSetUpUtil.createUser("anotherId1234", password, "01012341111", name, birthday, isMale);
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(newName);

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(anotherUser, updateUserRequest, file))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("프로필을 수정할 권한이 없습니다.");
    }


    @Test
    @DisplayName("앱 탈퇴를 한다")
    void deleteUser() throws AccessDeniedException {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);

        // WHEN
        userService.deleteUser(user);

        // THEN
        assertThat(user.getIsDelUser()).isTrue();
    }

    @Test
    @DisplayName("탈퇴 권한이 없는 유저가 앱 탈퇴를 시도하여 실패한다")
    @Disabled
    void deleteUserFail() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        User anotherUser = mockSetUpUtil.createUser("anotherId1234", password, "01012341111", name, birthday, isMale);

        assertThatThrownBy(() -> userService.deleteUser(anotherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("앱을 탈퇴할 권한이 없습니다.");
    }

    @Test
    @DisplayName("아이디 중복 여부를 확인한다")
    void checkAccountId() {
        User existUser = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        CheckAccountIdRequest checkAccountIdRequest = new CheckAccountIdRequest(existUser.getAccountId());

        // WHEN
        AccountDuplicatedResponse response = new AccountDuplicatedResponse(userService.isDuplicatedAccountId(checkAccountIdRequest.getAccountId()));

        // THEN
        assertThat(response.isDuplicated()).isTrue();
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    void changePassword() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        user.encodePassword(passwordEncoder);

        String changingPassword = "changePassword!123";
        ChangePasswordRequest request = new ChangePasswordRequest(accountId, changingPassword);

        // WHEN
        userService.changePassword(request);

        // THEN
        assertThat(passwordEncoder.matches(changingPassword, user.getPassword())).isTrue();

    }

    @Test
    @DisplayName("새로 넣은 비빌번호가 형식에 맞지 않아 비밀번호 변경에 실패한다")
    void changePasswordFailWhenUnValidatedPassword() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        user.encodePassword(passwordEncoder);

        String changingPassword = "!123";
        ChangePasswordRequest request = new ChangePasswordRequest(accountId, changingPassword);

        // WHEN & THEN
        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("비밀번호로 영문, 숫자, 특수문자 포함 8자 이상을 입력해주세요.");
    }

    @Test
    @DisplayName("기존 비밀번호를 그대로 넣어 비밀번호 변경에 실패한다")
    void changePasswordFailWhenSamePassword() {
        // GIVEN
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        user.encodePassword(passwordEncoder);

        String changingPassword = password;
        ChangePasswordRequest request = new ChangePasswordRequest(accountId, changingPassword);

        // WHEN & THEN
        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(MatchPasswordException.class)
                .hasMessage("기존 비밀번호와 일치합니다. 다른 비밀번호를 입력해주세요.");
    }

    @Test
    @DisplayName("휴대폰번호를 변경한다")
    void changePhoneNumber() throws AccessDeniedException {
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        String changingPhoneNumber ="01022223333";
        ChangePhoneNumberRequest request = new ChangePhoneNumberRequest(changingPhoneNumber);

        userService.changePhoneNumber(request, user);

        assertThat(user.getPhoneNumber()).isEqualTo(changingPhoneNumber);
    }

    @Test
    @DisplayName("수정 권한이 없는 유저가 휴대폰번호 변경을 시도하다가 실패한다.")
    @Disabled
    void changePhoneNumberFailWithNoAuthority() {
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        User anotherUser = mockSetUpUtil.createUser("anotherId1234", password, "01012341111", name, birthday, isMale);
        String changingPhoneNumber ="01022223333";
        ChangePhoneNumberRequest request = new ChangePhoneNumberRequest(changingPhoneNumber);

        assertThatThrownBy(() -> userService.changePhoneNumber(request, anotherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("휴대폰 번호를 변경할 권한이 없습니다.");
    }

    @Test
    @DisplayName("존재하는 계정의 휴대폰번호와 이름으로 변경을 시도하다가 실패한다.")
    void changePhoneNumberFailWithDuplicatedPhoneNumber() {
        User user = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        User anotherUser = mockSetUpUtil.createUser("anotherId1234", password, "01012341111", name, birthday, isMale);
        String changingPhoneNumber = phoneNumber;
        ChangePhoneNumberRequest request = new ChangePhoneNumberRequest(changingPhoneNumber);

        assertThatThrownBy(() -> userService.changePhoneNumber(request, anotherUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가입한 계정이 존재합니다.");
    }

}