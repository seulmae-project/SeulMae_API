package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class ChangePhoneNumberRequest {
    private String phoneNumber;

    public ChangePhoneNumberRequest() {
    }

    public ChangePhoneNumberRequest(String phoneNumber) {
        this.phoneNumber = setPhoneNumber(phoneNumber);
    }

    private String setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.replaceAll("-", "");
        return phoneNumber;
    }
}
