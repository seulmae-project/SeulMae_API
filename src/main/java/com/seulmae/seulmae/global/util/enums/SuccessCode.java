package com.seulmae.seulmae.global.util.enums;

import lombok.Getter;

@Getter
public enum SuccessCode {
    /**
     * ******************************* Success CodeList ***************************************
     */
    // 삽입 성공 코드 (HTTP Response: 201 Created)
    INSERT_SUCCESS(201, "S001", "INSERT SUCCESS"),
    // 조회 성공 코드 (HTTP Response: 200 OK)
    SELECT_SUCCESS(200, "S002", "SELECT SUCCESS"),
    //수정 성공 코드 (HTTP Response: 200 OK)
    UPDATE_SUCCESS(200, "S003", "UPDATE SUCCESS"),
    // 삭제 성공 코드 (HTTP Response: 200 OK)
    DELETE_SUCCESS(200, "S004", "DELETE SUCCESS"),


    // 로그인 성공 코드 (HTTP Response: 200 OK)
    LOGIN_SUCCESS(200, "S005", "LOGIN SUCCESS"),
    SIGNUP_SUCCESS(201, "S006", "SIGNUP SUCCESS"),
    LOGOUT_SUCCESS(200, "S007", "LOGOUT SUCCESS"),

    // Refresh Token 생성 성공 코드 (HTTP Response: 200 OK)
    REFRESH_TOKEN_SUCCESS(200, "S008", "REFRESH TOKEN SUCCESS"),

    //엑셀 등 파일 생성 성공 코드
    GENERATE_SUCCESS(200, "S009", "GENERATE SUCCESS"),

    SEND_SMS_SUCCESS(200, "S010", "SMS sent successfully!"),
    VERIFY_SMS_SUCCESS(200, "S011", "SMS verify successfully!"),

    SEND_SUCCESS(200, "S012", "SEND SUCCESS");

    /**
     * ******************************* Success Code Constructor ***************************************
     */
    // 성공 코드의 '코드 상태'를 반환한다.
    private final int status;

    // 성공 코드의 '코드 값'을 반환한다.
    private final String customStatus;

    // 성공 코드의 '코드 메시지'를 반환한다.s
    private final String message;

    // 생성자 구성
    SuccessCode(final int status, final String customStatus, final String message) {
        this.status = status;
        this.customStatus = customStatus;
        this.message = message;
    }
}
