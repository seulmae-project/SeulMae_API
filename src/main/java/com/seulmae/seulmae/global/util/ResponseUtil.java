package com.seulmae.seulmae.global.util;

import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class ResponseUtil {

    public static <T> ResponseEntity<SuccessResponse<T>> createSuccessResponse(SuccessCode successCode, T data) {
        SuccessResponse<T> response = new SuccessResponse<>(successCode, data);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public static ResponseEntity<SuccessResponse<Void>> createSuccessResponse(SuccessCode successCode) {
        SuccessResponse<Void> response = new SuccessResponse<>(successCode);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public static ResponseEntity<?> handleException(Exception e) {
        if (e instanceof AccessDeniedException) {
            return createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } else if (e instanceof AuthenticationException) {
            return createErrorResponse(ErrorCode.UNAUTHORIZED, e.getMessage());
        } else if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
            return createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        } else if (e instanceof NoSuchElementException) {
            return createErrorResponse(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
        } else {
            return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public static ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode, String errorDescription) {
        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus())
                .customStatus(errorCode.getCustomStatus())
                .message(errorCode.getMessage())
                .errorDescription(errorDescription)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
