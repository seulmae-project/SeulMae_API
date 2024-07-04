package com.seulmae.seulmae.global.util.enums;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SuccessResponse<T> {

    // API 응답 코드 Response
    private int status;

    // API 응답 코드 Message
    private String message;

    // API 응답 결과 Response
    private T data;

    // API 응답 시간
    private LocalDateTime timestamp;


    @Builder
    public SuccessResponse(final T data, final int status, final String message) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    @Builder
    public SuccessResponse(SuccessCode successCode) {
        this.status = successCode.getStatus();
        this.message = successCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }
    @Builder
    public SuccessResponse(SuccessCode successCode,T result) {
        this.status = successCode.getStatus();
        this.message = successCode.getMessage();
        this.data = result;
        this.timestamp = LocalDateTime.now();
    }

}
