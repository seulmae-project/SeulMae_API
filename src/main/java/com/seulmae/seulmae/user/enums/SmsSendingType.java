package com.seulmae.seulmae.user.enums;

public enum SmsSendingType {
    SIGNUP("signUp"),
    FIND_ACCOUNT_ID("findAccountId"),
    FIND_PW("findPassword"),
    CHANGE_PHONE_NUM("changePhoneNumber");
    private String sendingType;

    SmsSendingType(String sendingType) {
        this.sendingType = sendingType;
    }
}
