package com.seulmae.seulmae.workplace.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.dto.WorkplaceJoinRequestDto;
import com.seulmae.seulmae.workplace.service.WorkplaceJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
     * @param workplaceId
     */
    @PostMapping("request")
    public ResponseEntity<?> sendJoinRequest(@AuthenticationPrincipal User user, @RequestParam Long workplaceId) {
        try {
            workplaceJoinService.sendJoinRequest(user, workplaceId);

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
    public ResponseEntity<?> sendJoinApproval(@RequestParam Long workplaceApproveId) {
        try {
            workplaceJoinService.sendJoinApproval(workplaceApproveId);

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
     * 근무지 입장 요청 리스트
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
}
