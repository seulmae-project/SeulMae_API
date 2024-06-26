package com.seulmae.seulmae.notification.controller;

import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.notification.dto.request.FcmSendRequest;
import com.seulmae.seulmae.notification.service.NotificationService;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification/v1")
public class NotificationController {
    private final NotificationService notificationService;

    // 시스템 공지사항 보내기

    // 각 근무지 공지사항 보내기

    // 출퇴근 승인 요청 알림 보내기
    @PostMapping("/att-manage/apprv-req/send")
    public ResponseEntity<?> sendMessageToManagerForAttendanceRequest(@RequestBody FcmSendRequest request,
                                                                      @AuthenticationPrincipal User user) throws IOException {
        notificationService.sendMessageToManagerForAttendanceRequest(request, user);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SEND_SUCCESS), HttpStatus.OK);
    }
    // 출퇴근 확인 응답 보내기

    // 읽은 경우

    // 해당 유저의 알림 전체 리스트


    // 해당 유저의 알림 디테일

    // 알림 삭제

}
