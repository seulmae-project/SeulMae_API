package com.seulmae.seulmae.user.dto.request;

import com.seulmae.seulmae.user.enums.SmsSendingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class SmsSendingRequest {
    private SmsSendingType sendingType;
    private String name;
    private String phoneNumber;

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
