package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.user.dto.request.ManagerDelegationRequest;
import com.seulmae.seulmae.user.dto.response.UserInfoWithWorkplaceResponse;
import com.seulmae.seulmae.user.dto.response.UserWorkplaceUserResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.service.UserWorkplaceService;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workplace/user/v1")
public class UserWorkplaceController {
    private final UserWorkplaceService userWorkplaceService;

    @GetMapping("")
    public ResponseEntity<?> getUserInfoWithWorkplace(@RequestParam Long userWorkplaceId, HttpServletRequest request) {
        try {
            UserInfoWithWorkplaceResponse result = userWorkplaceService.getUserInfoByUserWorkplace(userWorkplaceId, request);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, result);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    /**
     * 매니저 권한 위임
     */
    @PostMapping("manager/delegate")
    public ResponseEntity<?> delegateManagerAuthority(@AuthenticationPrincipal User user, @RequestBody ManagerDelegationRequest managerDelegationRequest) {
        try {
            userWorkplaceService.delegateManagerAuthority(user, managerDelegationRequest);
            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    /**
     * 근무지에 포함된 모든 유저 리스트
     */
    @GetMapping("list")
    public ResponseEntity<?> getAllUserFromWorkplace(@RequestParam Long workplaceId, HttpServletRequest httpServletRequest) {
        try {
            List<UserWorkplaceUserResponse> userWorkplaceUserResponseList = userWorkplaceService.getAllUserFromWorkplace(workplaceId, httpServletRequest);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, userWorkplaceUserResponseList);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }
}
