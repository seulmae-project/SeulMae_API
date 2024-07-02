package com.seulmae.seulmae.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.AuthenticationHelper;
import com.seulmae.seulmae.user.MockUser;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest // 테스트용 애플리케이션 컨텍스트 생성
@AutoConfigureMockMvc // MockMVC 생성 및 자동구성
class UserControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp() throws Exception {
        // given
        final String url = "/api/users";

        final String accountId = "test1234";
        final String password = "qwer1234!";
        final String phoneNumber = "01012341234";
        final String name = "이름";
        final Boolean isMale = true;
        final String birthday = "19931221";
        final UserSignUpDto userSignUpDto = new UserSignUpDto(accountId, password, phoneNumber, name, isMale, birthday);

        final String requestBody = objectMapper.writeValueAsString(userSignUpDto);

        MockMultipartFile userSignUpDtoPart = new MockMultipartFile("userSignUpDto", "userSignUpDto", "application/json", requestBody.getBytes());


        // when
        final ResultActions result = mockMvc.perform(multipart(url)
                .file(userSignUpDtoPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isCreated());

        List<User> users = userRepository.findAll();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.getFirst().getAccountId()).isEqualTo(accountId);
        assertThat(users.getFirst().getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(users.getFirst().getAuthorityRole()).isEqualTo(Role.USER);

    }

    @Test
    @DisplayName("회원가입 실패- 아이디 중복")
    void signUpFail() throws Exception {
        // given
        final String url = "/api/users";

        final String accountId = "test1234";
        final String password = "qwer1234!";
        final String phoneNumber = "01012341234";
        final String name = "이름";
        final Boolean isMale = true;
        final String birthday = "19931221";

        userRepository.save(User.builder()
                .accountId(accountId)
                .password(password)
                .phoneNumber("01052352222")
                .name("다른이름")
                .isMale(isMale)
                .birthday("19651211")
                .authorityRole(Role.USER)
                .build());

        final UserSignUpDto userSignUpDto = new UserSignUpDto(accountId, password, phoneNumber, name, isMale, birthday);

        final String requestBody = objectMapper.writeValueAsString(userSignUpDto);

        MockMultipartFile userSignUpDtoPart = new MockMultipartFile("userSignUpDto", "userSignUpDto", "application/json", requestBody.getBytes());


        // when
        final ResultActions result = mockMvc.perform(multipart(url)
                .file(userSignUpDtoPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }


    @Test
    @MockUser()
    @DisplayName("프로필 수정 - 성공")
    void updateProfile() throws Exception {

        // given
        final String url = "/api/users";
        final String newName = "수정이름";

        User actualLoginMember = authenticationHelper.getCurrentUser();

        UpdateUserRequest request = new UpdateUserRequest(newName);
        String requestBody = objectMapper.writeValueAsString(request);
        MockMultipartFile updateUserRequestPart = new MockMultipartFile("updateUserRequest", "updateUserRequest", "application/json", requestBody.getBytes());

        // when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(updateUserRequestPart)
                .with(_request -> {
                    _request.setMethod("PUT");
                    return _request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(actualLoginMember.getIdUser())));

        // then
        result.andExpect(status().isCreated());
        assertThat(actualLoginMember.getName()).isEqualTo(newName);

    }

    @Test
    @DisplayName("프로필 조회 - 성공")
    @MockUser(accountId = "test2222", phoneNumber = "01011111111")
    void getUserProfile() throws Exception {
        final String url = "/api/users";

        final String accountId = "test1234";
        final String password = "qwer1234!";
        final String phoneNumber = "01012341234";
        final String name = "이름";
        final Boolean isMale = true;
        final String birthday = "19931221";

        User savedUser = userRepository.save(User.builder()
                .accountId(accountId)
                .password(password)
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(isMale)
                .birthday(birthday)
                .authorityRole(Role.USER)
                .build());

        ResultActions result = mockMvc.perform(get(url)
                .param("id", String.valueOf(savedUser.getIdUser())));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.phoneNumber").value(phoneNumber));
    }

//    @Test
//    void sendSMS() {
//    }
//
//    @Test
//    void verifySMS() {
//    }

//    @Test
//    void updateAdditionalProfileData() {
//    }

    @Test
    @DisplayName("아이디 중복 확인 - 중복됨")
    void checkAccountId() throws Exception {
        final String url = "/api/users/id/duplication";

        final String accountId = "change";
        final String password = "qwer1234!";
        final String phoneNumber = "01012341234";
        final String name = "이름";
        final Boolean isMale = true;
        final String birthday = "19931221";

        final String checkAccountId = "change";

        userRepository.save(User.builder()
                .accountId(accountId)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(isMale)
                .birthday(birthday)
                .authorityRole(Role.USER)
                .build());

        CheckAccountIdRequest checkAccountIdRequest = new CheckAccountIdRequest(checkAccountId);
        String request = objectMapper.writeValueAsString(checkAccountIdRequest);
        ResultActions result = mockMvc.perform(post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicated").value(true));
    }

    @Test
    @DisplayName("아이디 중복 확인 - 중복아님")
    void checkNotDuplicatedAccountId() throws Exception {
        final String url = "/api/users/id/duplication";

        final String accountId = "change";
        final String password = "qwer1234!";
        final String phoneNumber = "01012341234";
        final String name = "이름";
        final Boolean isMale = true;
        final String birthday = "19931221";

        String checkAccountId = "notchange";

        userRepository.save(User.builder()
                .accountId(accountId)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(isMale)
                .birthday(birthday)
                .authorityRole(Role.USER)
                .build());

        CheckAccountIdRequest checkAccountIdRequest = new CheckAccountIdRequest(checkAccountId);
        String request = objectMapper.writeValueAsString(checkAccountIdRequest);

        ResultActions result = mockMvc.perform(post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicated").value(false));
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void changePassword() throws Exception {
        final String url = "/api/users/pw";

        final String accountId = "test1234";
        final String password = "qwer1234!";
        final String phoneNumber = "01012341234";
        final String name = "이름";
        final Boolean isMale = true;
        final String birthday = "19931221";

        final String changePassword = "change1234!";

        User savedUser = userRepository.save(User.builder()
                .accountId(accountId)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(isMale)
                .birthday(birthday)
                .authorityRole(Role.USER)
                .build());

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(accountId, changePassword);
        String request = objectMapper.writeValueAsString(changePasswordRequest);

        ResultActions result = mockMvc.perform(put(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        result.andExpect(status().isOk());

        User finalUser = userRepository.findById(savedUser.getIdUser()).get();
        assertThat(passwordEncoder.matches(changePassword, finalUser.getPassword())).isTrue();
    }

    @Test
    @MockUser()
    @DisplayName("유저 삭제 - 성공")
    void deleteUser() throws Exception {
        // given
        final String url = "/api/users";

        User actualLoginMember = authenticationHelper.getCurrentUser();

        // when
        ResultActions result = mockMvc.perform(delete(url)
                .param("id", String.valueOf(actualLoginMember.getIdUser())));


        // then
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isNoContent());
        assertThat(actualLoginMember.getIsDelUser()).isTrue();
    }

    @Test
    @MockUser()
    @DisplayName("휴대폰번호 변경 - 성공")
    void changePhoneNumber() throws Exception {
        final String url = "/api/users/phone";
        final String changePhoneNumber = "01063463222";

        User actualLoginMember = authenticationHelper.getCurrentUser();

        ChangePhoneNumberRequest changePhoneNumberRequest = new ChangePhoneNumberRequest(changePhoneNumber);
        String request = objectMapper.writeValueAsString(changePhoneNumberRequest);

        ResultActions result = mockMvc.perform(put(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .param("id", String.valueOf(actualLoginMember.getIdUser())));

        result.andExpect(status().isOk());
        assertThat(actualLoginMember.getPhoneNumber()).isEqualTo(changePhoneNumber);
    }
}