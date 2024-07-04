package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.dto.request.UserWorkScheduleAddRequest;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.service.UserWorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/schedule/v1")
public class UserWorkScheduleController {
    private final UserWorkScheduleService userWorkScheduleService;

    // 알바생 일정에 추가
    @PostMapping("")
    public ResponseEntity<?> addUserWorkSchedule(@RequestBody UserWorkScheduleAddRequest userWorkScheduleAddRequest,
                                                 @AuthenticationPrincipal User user) {
        userWorkScheduleService.addUserWorkSchedule(userWorkScheduleAddRequest, user);
        SuccessResponse successResponse = new SuccessResponse<>(SuccessCode.INSERT_SUCCESS);
        return ResponseEntity.status(successResponse.getStatus()).body(successResponse);
    }

    // 알바생 일정에 삭제
    @DeleteMapping("")
    public ResponseEntity<?> deleteUserWorkSchedule(@RequestParam Long userWorkScheduleId,
                                                    @AuthenticationPrincipal User user) {
        userWorkScheduleService.deleteUserWorkSchedule(userWorkScheduleId, user);
        SuccessResponse successResponse = new SuccessResponse<>(SuccessCode.DELETE_SUCCESS);
        return ResponseEntity.status(successResponse.getStatus()).body(successResponse);
    }
}
