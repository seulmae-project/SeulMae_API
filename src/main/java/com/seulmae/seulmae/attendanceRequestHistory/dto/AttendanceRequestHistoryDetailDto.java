package com.seulmae.seulmae.attendanceRequestHistory.dto;

public class AttendanceRequestHistoryDetailDto {
    // 시급
    private final Integer baseWage;
    // 전달 사항
    private final String deliveryMessage;
    // 메모
    private final String attendanceRequestMemo;
    public AttendanceRequestHistoryDetailDto(Integer baseWage,
                                             String deliveryMessage,
                                             String attendanceRequestMemo) {
        this.baseWage = baseWage;
        this.deliveryMessage = deliveryMessage;
        this.attendanceRequestMemo = attendanceRequestMemo;
    }
}
