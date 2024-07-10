package com.seulmae.seulmae.global.util.enums;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;                 // 에러 상태 코드
    private String customStatus;        // 에러 구분 코드
    private String message;             // 에러 메시지
    private String errorDescription;    // 에러 이유
    private LocalDateTime timestamp;    // 에러 발생 시간
    //    private List<FieldError> errors;    // 상세 에러 메시지


    /**
     * ErrorResponse 생성자-1
     *
     * @param code ErrorCode
     */
    @Builder
    protected ErrorResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.customStatus = code.getCustomStatus();
        this.timestamp = LocalDateTime.now();
//        this.errors = new ArrayList<>();
    }

    /**
     * ErrorResponse 생성자-2
     *
     * @param code   ErrorCode
     * @param errorDescription String
     */
    @Builder
    public ErrorResponse(final ErrorCode code, final String errorDescription) {
        this.status = code.getStatus();
        this.customStatus = code.getCustomStatus();
        this.message = code.getMessage();
        this.errorDescription = errorDescription;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * ErrorResponse 생성자-3
     *
     * @param code   ErrorCode
     * @param errors List<FieldError>
     */
//    @Builder
//    protected ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
//        this.message = code.getMessage();
//        this.status = code.getStatus();
////        this.errors = errors;
//        this.customStatus = code.getDivisionCode();
//    }


    /**
     * Global Exception 전송 타입-1
     *
     * @param code          ErrorCode
     * @param bindingResult BindingResult
     * @return ErrorResponse
     */
//    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
//        return new ErrorResponse(code, FieldError.of(bindingResult));
//    }

    /**
     * Global Exception 전송 타입-2
     *
     * @param code ErrorCode
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    /**
     * Global Exception 전송 타입-3
     *
     * @param code   ErrorCode
     * @param reason String
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }


    /**
     * 에러를 e.getBindingResult() 형태로 전달 받는 경우 해당 내용을 상세 내용으로 변경하는 기능을 수행한다.
     */
//    @Getter
//    public static class FieldError {
//        private final String field;
//        private final String value;
//        private final String reason;
//
//        public static List<FieldError> of(final String field, final String value, final String reason) {
//            List<FieldError> fieldErrors = new ArrayList<>();
//            fieldErrors.add(new FieldError(field, value, reason));
//            return fieldErrors;
//        }
//
//        private static List<FieldError> of(final BindingResult bindingResult) {
//            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
//            return fieldErrors.stream()
//                    .map(error -> new FieldError(
//                            error.getField(),
//                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
//                            error.getDefaultMessage()))
//                    .collect(Collectors.toList());
//        }
//
//        @Builder
//        FieldError(String field, String value, String reason) {
//            this.field = field;
//            this.value = value;
//            this.reason = reason;
//        }
//    }
}
