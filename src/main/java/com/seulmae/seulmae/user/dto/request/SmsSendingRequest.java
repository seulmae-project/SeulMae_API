package com.seulmae.seulmae.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class SmsSendingRequest {
    private String sendingType;
    private String phoneNumber;
    private String accountId;

    public SmsSendingRequest(String sendingType, String phoneNumber) {
        this.sendingType = sendingType;
        this.phoneNumber = phoneNumber;
    }

    public SmsSendingRequest(String sendingType, String phoneNumber, String accountId) {
        this.sendingType = sendingType;
        this.phoneNumber = phoneNumber;
        this.accountId = accountId;
    }

    public String setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
        return phoneNumber;
    }
}
