package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class SmsSendingForPasswordRequest extends SmsSendingRequest {
    private String accountId;

    public SmsSendingForPasswordRequest(String sendingType, String phoneNumber, String accountId) {
        super(sendingType, phoneNumber, accountId);
    }
}
