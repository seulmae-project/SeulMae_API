package com.seulmae.seulmae.global.handler;

import com.seulmae.seulmae.global.exception.AttendanceRequestConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @ControllerAdvice 전역 예외 처리, 전역 데이터 바인딩, 전역 모델 속성 등을 위해 사용되는 어노테이션
 * 컨트롤러 전반에 걸쳐 공통된 예외 처리 로직을 정의할 수 있음
 * **/
@ControllerAdvice
public class CustomExceptionHandler {

    /**
     * @ExceptionHandler 특정 예외를 처리하는 메서드를 정의하는 데 사용
     * @ResponseStatus HTTP 응답 상태 코드를 설정하는 데 사용
     * **/
    @ExceptionHandler(AttendanceRequestConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAttendanceRequestConflictException(AttendanceRequestConflictException ex) {
        return ex.getMessage();
    }
}
