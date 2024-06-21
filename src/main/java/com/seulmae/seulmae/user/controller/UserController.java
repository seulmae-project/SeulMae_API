package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.dto.response.UserProfileResponse;
import com.seulmae.seulmae.user.entity.CustomOAuth2User;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.exception.InvalidPasswordException;
import com.seulmae.seulmae.user.exception.MatchPasswordException;
import com.seulmae.seulmae.user.service.SmsService;
import com.seulmae.seulmae.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SmsService smsService;

    /**
     * 회원가입
     * @param userSignUpDto
     * @param file
     * @return
     */
    @PostMapping("")
    public ResponseEntity<?> signUp(@RequestPart UserSignUpDto userSignUpDto,  @RequestPart(required = false, name = "file") MultipartFile file) {
        userService.createUser(userSignUpDto, file);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SIGNUP_SUCCESS), HttpStatus.CREATED);
    }

    // 로그아웃
    // 이건 프론트에서 알아서 토큰을 스토리지에서 지우면 됨 ^^

    /**
     * 프로필 수정
     * @param id
     * @param updateUserRequest
     * @param file
     * @param user
     * @return
     * @throws AccessDeniedException
     */
    @PutMapping("")
    public ResponseEntity<?>updateProfile(@RequestParam Long id,
                                          @RequestPart UpdateUserRequest updateUserRequest,
                                          @RequestPart(required = false, name ="file") MultipartFile file,
                                          @AuthenticationPrincipal User user) throws AccessDeniedException {

        userService.updateUser(id, user.getIdUser(), updateUserRequest, file);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 유저 프로필 단일 조회
     * @param id
     * @param request
     * @return
     */
    @GetMapping("")
    public ResponseEntity<?>getUserProfile(@RequestParam Long id,
                                           HttpServletRequest request) {
        UserProfileResponse result = userService.getUserProfile(id, request);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SELECT_SUCCESS, result), HttpStatus.OK);
    }


    /**
     * 휴대폰 인증번호 신청
     * @param request
     * @return
     */
    @PostMapping("/sms-certification/send")
    public ResponseEntity<?> sendSMS(@RequestBody SmsSendingRequest request) {
        smsService.sendSMS(request.setPhoneNumber(request.getPhoneNumber()));
        FindAuthResponse result = userService.getEmail(request.getPhoneNumber());
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SEND_SMS_SUCCESS, result), HttpStatus.OK);
    }

    /**
     * 휴대폰 인증번호 확인
     * @param request
     * @return
     */
    @PostMapping("/sms-certification/confirm")
    public ResponseEntity<?> verifySMS(@RequestBody SmsCertificationRequest request) {
        smsService.verifySMS(request);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.VERIFY_SMS_SUCCESS), HttpStatus.OK);
    }

    /**
     * 소셜로그인 추가 정보 업데이트
     * @param oAuth2AdditionalDataRequest
     * @param file
     * @param user
     * @return
     */
    @PutMapping("/extra-profile")
    public ResponseEntity<?> updateAdditionalProfileData(@RequestPart OAuth2AdditionalDataRequest oAuth2AdditionalDataRequest,
                                                         @RequestPart(required = false, name = "file") MultipartFile file,
                                                         @AuthenticationPrincipal User user) {
        userService.updateAdditionalProfile(user.getSocialId(), user.getSocialType(), oAuth2AdditionalDataRequest, file);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.CREATED);
    }

    /**
     * 이메일 중복 확인
     * @param request
     * @return
     */
    @PostMapping("/email/duplication")
    public ResponseEntity<?> checkEmail(@RequestBody CheckEmailRequest request) {
       Boolean result = userService.isDuplicatedEmail(request.getEmail());
       return new ResponseEntity<>(new SuccessResponse(SuccessCode.INSERT_SUCCESS, result), HttpStatus.OK);
    }

    /**
     * 비밀번호 변경
     * @param request
     * @return
     */
    @PutMapping("/pw")
    public ResponseEntity<?>changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.OK);
        } catch (MatchPasswordException | InvalidPasswordException e) {
            return new ResponseEntity<>(new ErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * 회원탈퇴
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("")
    public ResponseEntity<?> deleteUser(@RequestParam Long id,
                                        @AuthenticationPrincipal User user) {
        try {
            userService.deleteUser(id, user);
            return new ResponseEntity<>(new SuccessResponse(SuccessCode.DELETE_SUCCESS), HttpStatus.NO_CONTENT);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ErrorResponse(ErrorCode.UNAUTHORIZED, e.getMessage()), HttpStatus.UNAUTHORIZED);
        }

    }

    /**
     * 휴대폰 번호 변경
     * @param id
     * @param request
     * @param user
     * @return
     */
    @PutMapping("/phone")
    public ResponseEntity<?>changePhoneNumber(@RequestParam Long id,
                                              @RequestBody ChangePhoneNumberRequest request,
                                              @AuthenticationPrincipal User user) {
        try {
            userService.changePhoneNumber(id, request, user);
            return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ErrorResponse(ErrorCode.UNAUTHORIZED, e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    // TODO: 유저의 프로필 데이터 조회

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