package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.entity.CustomOAuth2User;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.exception.InvalidPasswordException;
import com.seulmae.seulmae.user.exception.MatchPasswordException;
import com.seulmae.seulmae.user.service.SmsService;
import com.seulmae.seulmae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SmsService smsService;

    @PostMapping("")
    public ResponseEntity<?> signUp(@RequestBody UserSignUpDto userSignUpDto) {
        userService.createUser(userSignUpDto);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SIGNUP_SUCCESS), HttpStatus.CREATED);
    }

    // 로그아웃
    // 이건 프론트에서 알아서 토큰을 db에서 지우면 됨 ^^
    // 아니면 레디스로 내가 구현해야함 ㅡㅡ

    // 프로필 수정
    @PutMapping("")
    public ResponseEntity<?>updateProfile(@RequestParam Long id,
                                          @RequestBody UpdateUserRequest request,
                                          @AuthenticationPrincipal User user) throws AccessDeniedException {

        userService.updateUser(id, user.getIdUser(), request);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.OK);
    }


    @PostMapping("/sms-certification/send")
    public ResponseEntity<?> sendSMS(@RequestBody SmsSendingRequest request) {
        smsService.sendSMS(request.setPhoneNumber(request.getPhoneNumber()));
        FindAuthResponse result = userService.getEmail(request.getPhoneNumber());
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SEND_SMS_SUCCESS, result), HttpStatus.OK);
    }

    @PostMapping("/sms-certification/confirm")
    public ResponseEntity<?> verifySMS(@RequestBody SmsCertificationRequest request) {
        smsService.verifySMS(request);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.VERIFY_SMS_SUCCESS), HttpStatus.OK);
    }

    @PutMapping("/extra-profile")
    public ResponseEntity<?> updateAdditionalProfileData(@RequestBody OAuth2AdditionalDataRequest request,
                                                         @AuthenticationPrincipal User user) {
        userService.updateAdditionalProfile(user.getSocialId(), user.getSocialType(), request);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.CREATED);
    }
    @PostMapping("/email/duplication")
    public ResponseEntity<?> checkEmail(@RequestBody CheckEmailRequest request) {
       Boolean result = userService.isDuplicatedEmail(request.getEmail());
       return new ResponseEntity<>(new SuccessResponse(SuccessCode.INSERT_SUCCESS, result), HttpStatus.OK);
    }

    @PutMapping("/pw")
    public ResponseEntity<?>changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.OK);
        } catch (MatchPasswordException | InvalidPasswordException e) {
            return new ResponseEntity<>(new ErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/api/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }
}
//    @PostMapping("/email/search")
//    public ResponseEntity<?>searchEmail(@RequestBody SearchAuthRequest request) {
//        FindAuthResponse result = userService.getEmail(request.getPhoneNumber());
//        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SELECT_SUCCESS, result), HttpStatus.OK);
//    }

// 인증번호 발송
// 일반 회원가입 - 인증번호 확인
// 소셜로그인 추가 정보 입력 후 유저 정보 수정
// 이메일 중복 확인
// 아이디 찾기 - 이메일 조회
// 비번 찾기 - 비밀번호 변경