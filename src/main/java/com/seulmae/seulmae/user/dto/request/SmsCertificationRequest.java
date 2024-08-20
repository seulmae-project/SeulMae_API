package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class SmsCertificationRequest {
    private String sendingType;
    private String phoneNumber;
    private String authCode;

    public String setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
        return phoneNumber;
    }
}
