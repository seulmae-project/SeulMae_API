package com.seulmae.seulmae.attendance.controller;

import com.seulmae.seulmae.attendance.dto.AttendanceApprovalDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto;
import com.seulmae.seulmae.attendance.dto.GetOffWorkDto;
import com.seulmae.seulmae.attendance.service.AttendanceService;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        attendanceService.goToWork(user, workplaceId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무자 퇴근
     * @param getOffWorkDto
     */
    @PostMapping("finish")
    public ResponseEntity<?> getOffWork(@AuthenticationPrincipal User user, @RequestBody GetOffWorkDto getOffWorkDto) {
        attendanceService.getOffWork(user, getOffWorkDto);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무자 출/퇴근 승인
     * @param attendanceApprovalDto
     */
    @PostMapping("manager/approval")
    public ResponseEntity<?> sendAttendanceApproval(@RequestBody AttendanceApprovalDto attendanceApprovalDto) {
        attendanceService.sendAttendanceApproval(attendanceApprovalDto);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무자 출/퇴근 거절
     * @param attendanceRequestHistoryId
     */
    @PostMapping("manager/rejection")
    public ResponseEntity<?> sendAttendanceRejection(@RequestParam Long attendanceRequestHistoryId) {
        attendanceService.sendAttendanceRejection(attendanceRequestHistoryId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무자 출/퇴근 요청 리스트
     * @param workplaceId
     */
    @GetMapping("request/list")
    public ResponseEntity<?> getAttendanceRequestList(@RequestParam Long workplaceId) {
        List<AttendanceRequestListDto> attendanceRequestListDtoList = attendanceService.getAttendanceRequestList(workplaceId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, attendanceRequestListDtoList);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}
