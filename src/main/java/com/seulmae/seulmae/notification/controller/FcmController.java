package com.seulmae.seulmae.notification.controller;

import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.notification.dto.request.FcmSendRequest;
import com.seulmae.seulmae.notification.service.FcmIndividualServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/fcm/v1")
public class FcmController {
    private final FcmIndividualServiceImpl fcmIndividualServiceImpl;

    public FcmController(FcmIndividualServiceImpl fcmIndividualServiceImpl) {
        this.fcmIndividualServiceImpl = fcmIndividualServiceImpl;
    }


    // 모바일로부터 사용자의 데이터를 받아서 서비스 처리
//    @PostMapping("/send")
//    public ResponseEntity<?> pushMessage(@RequestBody @Validated FcmSendRequest fcmSendRequest) throws IOException {
//        log.debug("PUSH 메세지를 전송합니다.");
//        fcmIndividualServiceImpl.sendMessageTo(fcmSendRequest.getFcmToken(), fcmSendRequest.getTitle(), fcmSendRequest.getBody());
//        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SEND_SUCCESS), HttpStatus.OK);
//    }
}
