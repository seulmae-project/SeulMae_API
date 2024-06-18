package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsSendingRequest {
    private String phoneNumber;

    public String setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
        return phoneNumber;
    }
}
