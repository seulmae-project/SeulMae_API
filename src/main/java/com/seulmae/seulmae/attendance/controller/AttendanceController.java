package com.seulmae.seulmae.attendance.controller;

import com.seulmae.seulmae.attendance.dto.AttendanceApprovalDto;
import com.seulmae.seulmae.attendance.dto.AttendanceManagerMainListDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto;
import com.seulmae.seulmae.attendance.service.AttendanceService;
import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<?> sendAttendanceRejection(@RequestParam Long attendanceRequestHistoryId) {
        try {
            attendanceService.sendAttendanceRejection(attendanceRequestHistoryId);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무자 출/퇴근 요청 리스트
     * @param workplaceId
     */
    @GetMapping("request/list")
    public ResponseEntity<?> getAttendanceRequestList(@RequestParam Long workplaceId) {

        try {
            List<AttendanceRequestListDto> attendanceRequestListDtoList = attendanceService.getAttendanceRequestList(workplaceId);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, attendanceRequestListDtoList);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무자 별도 근무 요청
     *
     * @param user
     * @param attendanceRequestDto
     */
    @PostMapping("separate")
    public ResponseEntity<?> sendSeparateAttendanceRequest(@AuthenticationPrincipal User user, @RequestBody AttendanceRequestDto attendanceRequestDto) {
        try {
            attendanceService.sendSeparateAttendanceRequest(user, attendanceRequestDto);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 매니저 일별 근무자 요청 리스트
     **/
    @GetMapping("main/manager")
    public ResponseEntity<?> getDailyEmployeeAttendanceList(@RequestParam Workplace workplace, @RequestParam LocalDate localDate, HttpServletRequest request) {
        try {
            List<AttendanceManagerMainListDto> attendanceManagerMainListDtoList = attendanceService.getDailyEmployeeAttendanceList(workplace, localDate, request);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, attendanceManagerMainListDtoList);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }
}
