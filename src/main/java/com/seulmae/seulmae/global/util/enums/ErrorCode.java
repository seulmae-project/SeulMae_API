package com.seulmae.seulmae.global.util.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */
    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(400, "G001", "Bad Request Exception"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(400, "G002", "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(400, "G003", "Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "Missing Servlet RequestParameter Exception"),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(400, "G005", "I/O Exception"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(400, "G006", "JsonParseException"),

    // com.fasterxml.jackson.core Processing Error
    JACKSON_PROCESS_ERROR(400, "G007", "com.fasterxml.jackson.core Exception"),

    DUPLICATED_ERROR(409, "G008", "DUPLICATED_ERROR"),

    // 권한이 없음
    FORBIDDEN_ERROR(403, "G009", "Forbidden Exception"),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(500, "G999", "Internal Server Error Exception"),

    /**
     * 404
     */
    PARAMETERS_ARE_NOT_EXIST(404, "G016", "This request is missing required parameters."),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(404, "G0010", "Not Found Exception"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(404, "G011", "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(404, "G012", "Handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(404, "G013", "Header에 데이터가 존재하지 않는 경우"),

    ACCOUNT_BANNED(404, "G014", "Account is Banned"),

    ACCOUNT_IS_NOT_EXIST(404, "G015", "Account is Not Exist"),

    NO_SUCH_ELEMENT(404, "G016", "NO_SUCH_ELEMENT"),

    UNAUTHORIZED(401, "G022", "UNAUTHORIZED"),
    MethodArgumentNotValidException(422, "G017", "MethodArgumentNotValidException"),
    JwtTokenIsExpired(401,"G018" , "Jwt token is expired" ),
    JwtTokenIsNotValidException(401,"G019" ,"Jwt token is not valid" ),
    InvalidPasswordException(401,"G020" ,"Password does not match" ),
    NoSuchUserException(401,"G021" ,"No such user exist" );


    // 에러 코드의 '코드 상태'을 반환한다.
    private final int status;

    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private final String divisionCode;

    // 에러 코드의 '코드 메시지'을 반환한다.
    private final String message;

    // 생성자 구성
    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
