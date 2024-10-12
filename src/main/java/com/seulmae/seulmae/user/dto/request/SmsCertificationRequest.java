package com.seulmae.seulmae.user.dto.request;

import com.seulmae.seulmae.user.enums.SmsSendingType;
import lombok.Getter;

@Getter
public class SmsCertificationRequest {
    private SmsSendingType sendingType;
    private String phoneNumber;
    private String authCode;

    public String setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
        return phoneNumber;
    }
}
