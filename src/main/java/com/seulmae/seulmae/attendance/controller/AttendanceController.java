package com.seulmae.seulmae.attendance.controller;

import com.seulmae.seulmae.attendance.dto.AttendanceApprovalDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRejectionDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestDto;
import com.seulmae.seulmae.attendance.service.AttendanceService;
import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance/v1")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 근무자 출근
     * @param workplaceId
     */
    @PostMapping("attendance")
    public ResponseEntity<?> goToWork(@AuthenticationPrincipal User user, @RequestParam Long workplaceId) {
        try {
            attendanceService.goToWork(user, workplaceId);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무자 퇴근
     * @param attendanceRequestDto
     */
    @PostMapping("finish")
    public ResponseEntity<?> sendAttendanceRequest(@AuthenticationPrincipal User user, @RequestBody AttendanceRequestDto attendanceRequestDto) {
        try {
            attendanceService.sendAttendanceRequest(user, attendanceRequestDto);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무자 출/퇴근 승인
     * @param
     */
    @PostMapping("manager/approval")
    public ResponseEntity<?> sendAttendanceApproval(@RequestBody AttendanceApprovalDto attendanceApprovalDto) {
        try {
            attendanceService.sendAttendanceApproval(attendanceApprovalDto);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무자 출/퇴근 거절
     * @param
     */
    @PostMapping("manager/rejection")
    public ResponseEntity<?> sendAttendanceRejection(@RequestBody AttendanceRejectionDto attendanceRejectionDto) {
        try {
            attendanceService.sendAttendanceRejection(attendanceRejectionDto);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }
}
