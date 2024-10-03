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

    public SmsSendingRequest(SmsSendingType sendingType, String name, String phoneNumber) {
        this.sendingType = sendingType;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }


    public String setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
        return phoneNumber;
    }
}
