package com.seulmae.seulmae.attendanceRequestHistory.controller;

import com.seulmae.seulmae.attendanceRequestHistory.dto.*;
import com.seulmae.seulmae.attendanceRequestHistory.service.AttendanceHistoryService;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance/request-history")
public class AttendanceHistoryController {
    private final AttendanceHistoryService historyService;


    /*
    * 근무 달력
    * */
    @GetMapping("calender")
    public ResponseEntity<?> getCalender(@AuthenticationPrincipal User user,
                                         @RequestParam Long workplaceId,
                                         @RequestParam Integer year,
                                         @RequestParam Integer month) {
        try {
            AttendanceCalendarDto attendanceCalendarDto = historyService.getCalender(user, workplaceId, year, month);
            SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, attendanceCalendarDto);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*
     * 근무 현황
     * */
    @GetMapping("status")
    public ResponseEntity<?> getStatus(@AuthenticationPrincipal User user,
                                     @RequestParam Long workplaceId) {
        try {
            WorkStatusDto workStatusDto = historyService.getStatus(user, workplaceId);

            SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, workStatusDto);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("monthly")
    public ResponseEntity<?> getMonthly(@AuthenticationPrincipal User user,
                                        @RequestParam Long workplaceId,
                                        @RequestParam Integer year,
                                        @RequestParam Integer month) {
        try {
            MonthlyWorkSummaryDto monthlyWorkSummaryDto = historyService.getMonthlyWork(user, workplaceId, year, month);

            SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, monthlyWorkSummaryDto);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("list")
    public ResponseEntity<?> getHistoryList(@AuthenticationPrincipal User user,
                                            @RequestParam Long workplaceId,
                                            @RequestParam Integer year,
                                            @RequestParam Integer month,
                                            @RequestParam Integer page,
                                            @RequestParam Integer size) {
        try {
            AttendanceRequestHistoryDto historyList = historyService.getHistoryList(user, workplaceId, year, month, page, size);

            SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, historyList);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*
     * 근무 상세
     * */
    @GetMapping("detail")
    public ResponseEntity<?> getRequestHistoryDetail(@AuthenticationPrincipal User user,
                                       @RequestParam Long idAttendanceRequestHistory) {
        try {
            AttendanceRequestHistoryDetailDto historyDetail = historyService.getHistoryDetail(user, idAttendanceRequestHistory);

            SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, historyDetail);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    @PostMapping("detail-employee")
//    public ResponseEntity<?> updateDetailUser(@RequestBody User user) {
//        try {
//            historyService.updateDetailEmployee(user);
//            SuccessResponse successResponse = new SuccessResponse(SuccessCode.UPDATE_SUCCESS);
//            return ResponseEntity.ok(successResponse);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//    @PostMapping("detail-manager")
//    public ResponseEntity<?> updateDetailManager(@RequestBody User user) {
//        try {
//            historyService.updateDetailManager(user);
//            SuccessResponse successResponse = new SuccessResponse(SuccessCode.UPDATE_SUCCESS);
//            return ResponseEntity.ok(successResponse);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

}
