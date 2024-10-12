package com.seulmae.seulmae.user.dto.request;

import com.seulmae.seulmae.user.enums.SmsSendingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsSendingRequest {
    private SmsSendingType sendingType;
    private String name;
    private String phoneNumber;

    public String setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
        return phoneNumber;
    }
}
