package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.AccountDuplicatedResponse;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.dto.response.UserProfileResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.service.SmsService;
import com.seulmae.seulmae.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    /**
     * 회원가입
     *
     * @param userSignUpDto
     * @param file
     * @return
     */
    @PostMapping("")
    public ResponseEntity<?> signUp(@RequestPart UserSignUpDto userSignUpDto, @RequestPart(required = false, name = "file") MultipartFile file) {
        try {
            userService.createUser(userSignUpDto, file);
            return ResponseUtil.createSuccessResponse(SuccessCode.SIGNUP_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    // 로그아웃
    // 이건 프론트에서 알아서 토큰을 스토리지에서 지우면 됨 ^^

    /**
     * 프로필 수정
     *
     * @param updateUserRequest
     * @param file
     * @param user
     * @return
     */
    @PutMapping("")
    public ResponseEntity<?> updateProfile(@RequestPart UpdateUserRequest updateUserRequest,
                                           @RequestPart(required = false, name = "file") MultipartFile file,
                                           @AuthenticationPrincipal User user) {

        try {
            userService.updateUser(user, updateUserRequest, file);
            return ResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS);
        } catch (AccessDeniedException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    /**
     * 유저 프로필 단일 조회
     */
    @GetMapping("")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User user,
                                            HttpServletRequest request) {
        try {
            UserProfileResponse result = userService.getUserProfile(user, request);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, result);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

//    @GetMapping("")
//    public ResponseEntity<?> getUserProfile(@RequestParam Long id,
//                                            HttpServletRequest request) {
//        try {
//            UserProfileResponse result = userService.getUserProfile(id, request);
//            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, result);
//        } catch (Exception e) {
//            return ResponseUtil.handleException(e);
//        }
//    }



    /**
     * 회원탈퇴
     * @param user
     * @return
     */
    @DeleteMapping("")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal User user) {
        try {
            userService.deleteUser(user);
            return ResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS);
        } catch (AccessDeniedException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }


    /**
     * 휴대폰 인증번호 신청
     *
     * @param request
     * @return
     */
    @PostMapping("/sms-certification/send")
    public ResponseEntity<?> sendSMS(@RequestBody SmsSendingRequest request) {
        try {
            FindAuthResponse result = userService.sendSMSCertification(request);
            return ResponseUtil.createSuccessResponse(SuccessCode.SEND_SMS_SUCCESS, result);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 휴대폰 인증번호 확인
     *
     * @param request
     * @return
     */
    @PostMapping("/sms-certification/confirm")
    public ResponseEntity<?> verifySMS(@RequestBody SmsCertificationRequest request) {
        try {
            FindAuthResponse result = userService.confirmSMSCertification(request);
            return ResponseUtil.createSuccessResponse(SuccessCode.VERIFY_SMS_SUCCESS, result);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 소셜로그인 추가 정보 업데이트
     *
     * @param oAuth2AdditionalDataRequest
     * @param file
     * @param user
     * @return
     */
    @PutMapping("/extra-profile")
    public ResponseEntity<?> updateAdditionalProfileData(@RequestPart OAuth2AdditionalDataRequest oAuth2AdditionalDataRequest,
                                                         @RequestPart(required = false, name = "file") MultipartFile file,
                                                         @AuthenticationPrincipal User user) {
        try {
            userService.updateAdditionalProfile(user.getSocialId(), user.getSocialType(), oAuth2AdditionalDataRequest, file);
            return ResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 아이디 중복 확인
     *
     * @param request
     * @return
     */
    @PostMapping("/id/duplication")
    public ResponseEntity<?> checkAccountId(@RequestBody CheckAccountIdRequest request) {
        try {
            AccountDuplicatedResponse result = new AccountDuplicatedResponse(userService.isDuplicatedAccountId(request.getAccountId()));
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, result);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 비밀번호 변경
     *
     * @param request
     * @return
     */
    @PutMapping("/pw")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return ResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }

    }

    /**
     * 휴대폰 번호 변경
     *
     * @param id
     * @param request
     * @param user
     * @return
     */
    @PutMapping("/phone")
    public ResponseEntity<?> changePhoneNumber(@RequestParam Long id,
                                               @RequestBody ChangePhoneNumberRequest request,
                                               @AuthenticationPrincipal User user) {
        try {
            userService.changePhoneNumber(id, request, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS);
        } catch (AccessDeniedException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

}