package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.dto.request.UserWorkScheduleAddRequest;
import com.seulmae.seulmae.user.dto.request.UserWorkScheduleUpdateRequest;
import com.seulmae.seulmae.user.dto.response.UserWorkScheduleListResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.service.UserWorkScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/schedule/v1")
public class UserWorkScheduleController {
    private final UserWorkScheduleService userWorkScheduleService;

    // 해당 근무일정의 알바생 리스트
    @GetMapping("/list")
    public ResponseEntity<?> getUsersByWorkSchedule(@RequestParam Long workScheduleId,
                                                    @AuthenticationPrincipal User user,
                                                    HttpServletRequest request) {
        try {
            List<UserWorkScheduleListResponse> results = userWorkScheduleService.getUsersByWorkSchedule(workScheduleId, user, request);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, results);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }


    }

    // 알바생 일정에 추가
    @PostMapping("")
    public ResponseEntity<?> addUserWorkSchedule(@RequestBody UserWorkScheduleAddRequest userWorkScheduleAddRequest,
                                                 @AuthenticationPrincipal User user) {
        try {
            userWorkScheduleService.addUserWorkSchedule(userWorkScheduleAddRequest, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }


    // 알바생의 일정 수정
    @PatchMapping("")
    public ResponseEntity<?> modifyUserWorkSchedule(@RequestParam Long userWorkScheduleId,
                                                    @RequestBody UserWorkScheduleUpdateRequest userWorkScheduleUpdateRequest,
                                                    @AuthenticationPrincipal User user) {
        try {
            userWorkScheduleService.modifyUserWorkSchedule(userWorkScheduleId, userWorkScheduleUpdateRequest, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    // 알바생 일정에 삭제
    @DeleteMapping("")
    public ResponseEntity<?> deleteUserWorkSchedule(@RequestParam Long userWorkScheduleId,
                                                    @AuthenticationPrincipal User user) {
        try {
            userWorkScheduleService.deleteUserWorkSchedule(userWorkScheduleId, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }
}
