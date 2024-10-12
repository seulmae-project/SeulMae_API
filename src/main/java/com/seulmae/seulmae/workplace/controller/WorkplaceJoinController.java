package com.seulmae.seulmae.workplace.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.dto.*;
import com.seulmae.seulmae.workplace.service.WorkplaceJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workplace/join/v1")
public class WorkplaceJoinController {

    private final WorkplaceJoinService workplaceJoinService;

    /**
     * 근무지 입장 요청
     * @param workplaceJoinDto
     */
    @PostMapping("request")
    public ResponseEntity<?> sendJoinRequest(@AuthenticationPrincipal User user, @RequestBody WorkplaceJoinDto workplaceJoinDto) {
        try {
            workplaceJoinService.sendJoinRequest(user, workplaceJoinDto);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 입장 수락
     * @param workplaceApproveId
     */
    @PostMapping("approval")
    public ResponseEntity<?> sendJoinApproval(@RequestParam Long workplaceApproveId, @RequestBody JoinApprovalDto joinApprovalDto) {
        try {
            workplaceJoinService.sendJoinApproval(workplaceApproveId, joinApprovalDto);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 입장 거절
     * @param workplaceApproveId
     */
    @PostMapping("rejection")
    public ResponseEntity<?> sendJoinRejection(@RequestParam Long workplaceApproveId) {
        try {
            workplaceJoinService.sendJoinRejection(workplaceApproveId);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 입장 요청 전체 리스트
     * @param workplaceId
     */
    @GetMapping("request/list")
    public ResponseEntity<?> getWorkplaceRequestList(@RequestParam Long workplaceId) {
        try {
            List<WorkplaceJoinRequestDto> workplaceJoinRequestDtoList = workplaceJoinService.getWorkplaceRequestList(workplaceId);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, workplaceJoinRequestDtoList);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 입장 요청 내역 리스트
     * @param user
     */
    @GetMapping("request/history/list")
    public ResponseEntity<?> getJoinRequestList(@AuthenticationPrincipal User user) {
        try {
            List<WorkplaceJoinHistoryResponse> workplaceJoinHistoryResponseList = workplaceJoinService.getJoinRequestList(user);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, workplaceJoinHistoryResponseList);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }
}
