package com.seulmae.seulmae.user.enums;

public enum SmsSendingType {
    SIGNUP("회원가입"),
    FIND_ACCOUNT_ID("아이디 찾기"),
    CHANGE_PW("비밀번호 재설정"),
    CHANGE_PHONE_NUM("휴대폰번호 변경");
    private String sendingType;

    SmsSendingType(String sendingType) {
        this.sendingType = sendingType;
    }
}
