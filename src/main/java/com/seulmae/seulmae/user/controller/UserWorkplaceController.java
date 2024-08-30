package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.user.dto.request.ManagerDelegationRequest;
import com.seulmae.seulmae.user.dto.response.UserInfoWithWorkplaceResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.service.UserWorkplaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("")
    public ResponseEntity<?> withdrawWorkplace(@AuthenticationPrincipal User user, @RequestParam Long workplaceId) {
        try {
            userWorkplaceService.withdrawWorkplace(user, workplaceId);
            return ResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }
}
