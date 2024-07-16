package com.seulmae.seulmae.attendance.controller;

import com.seulmae.seulmae.attendance.dto.AttendanceRequestHistoryDto;
import com.seulmae.seulmae.attendance.service.AttendanceHistoryService;
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
@RequestMapping("/api/attendance/history")
public class AttendanceHistoryController {
    private final AttendanceHistoryService historyService;

    /*
     * 근무 현황 리스트
     * */
    @GetMapping("list")
    public ResponseEntity<?> getHistoryList(@AuthenticationPrincipal User user,
                                            @RequestParam Long workplaceId,
                                            @RequestParam Integer month,
                                            @RequestParam Integer year,
                                            @RequestParam Integer page,
                                            @RequestParam Integer size) {
        try {
            AttendanceRequestHistoryDto historyList = historyService.getHistoryList(user, workplaceId, month, year, page, size);

            SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, historyList);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*
     * 근무 상세
     * */



}
