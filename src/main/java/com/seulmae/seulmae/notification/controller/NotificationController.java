package com.seulmae.seulmae.notification.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.notification.dto.response.NotificationResponse;
import com.seulmae.seulmae.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification/v1")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 유저의 알림리스트(근무지 별)
     * @param userWorkplaceId
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<?> getNotifications(@RequestParam Long userWorkplaceId) {
        try {
            List<NotificationResponse> results = notificationService.getNotifications(userWorkplaceId);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, results);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }
}
