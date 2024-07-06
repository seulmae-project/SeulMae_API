package com.seulmae.seulmae.workplace.controller;

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
        workplaceJoinService.sendJoinRequest(user, workplaceId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무지 입장 수락
     * @param workplaceApproveId
     */
    @PostMapping("approval")
    public ResponseEntity<?> sendJoinApproval(@RequestParam Long workplaceApproveId) {
        workplaceJoinService.sendJoinApproval(workplaceApproveId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무지 입장 거절
     * @param workplaceApproveId
     */
    @PostMapping("rejection")
    public ResponseEntity<?> sendJoinRejection(@RequestParam Long workplaceApproveId) {
        workplaceJoinService.sendJoinRejection(workplaceApproveId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무지 입장 요청 리스트
     * @param workplaceId
     */
    @GetMapping("request/list")
    public ResponseEntity<?> getWorkplaceRequestList(@RequestParam Long workplaceId) {
        List<WorkplaceJoinRequestDto> workplaceJoinRequestDtoList = workplaceJoinService.getWorkplaceRequestList(workplaceId);

        SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, workplaceJoinRequestDtoList);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}
