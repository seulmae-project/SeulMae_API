package com.seulmae.seulmae.workplace.controller;

import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.service.WorkplaceJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        workplaceJoinService.sendJoinRequest(user, workplaceId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무지 입장 수락
     * @param workplaceApproveId
     * @param workplaceJoinHistoryId
     */
    @PostMapping("approval")
    public ResponseEntity<?> sendJoinApproval(@RequestParam Long workplaceApproveId, @RequestParam Long workplaceJoinHistoryId) {
        workplaceJoinService.sendJoinApproval(workplaceApproveId, workplaceJoinHistoryId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무지 입장 거절
     * @param workplaceApproveId
     * @param workplaceJoinHistoryId
     */
    @PostMapping("rejection")
    public ResponseEntity<?> sendJoinRejection(@RequestParam Long workplaceApproveId, @RequestParam Long workplaceJoinHistoryId) {
        workplaceJoinService.sendJoinRejection(workplaceApproveId, workplaceJoinHistoryId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }
}
