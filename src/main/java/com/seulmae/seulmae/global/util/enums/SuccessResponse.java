package com.seulmae.seulmae.global.util.enums;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessResponse<T> {

    // API 응답 결과 Response
    private T data;

    // API 응답 코드 Response
    private int status;

    // API 응답 코드 Message
    private String resultMsg;

    @Builder
    public SuccessResponse(final T data, final int status, final String resultMsg) {
        this.data = data;
        this.status = status;
        this.resultMsg = resultMsg;
    }

    @Builder
    public SuccessResponse(SuccessCode successCode) {
        this.status = successCode.getStatus();
        this.resultMsg = successCode.getMessage();
    }
    @Builder
    public SuccessResponse(SuccessCode successCode,T result) {
        this.status = successCode.getStatus();
        this.resultMsg = successCode.getMessage();
        this.data = result;
    }

}
