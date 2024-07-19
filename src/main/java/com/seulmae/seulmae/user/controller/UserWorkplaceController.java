package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.user.dto.response.UserInfoWithWorkplaceResponse;
import com.seulmae.seulmae.user.service.UserWorkplaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
