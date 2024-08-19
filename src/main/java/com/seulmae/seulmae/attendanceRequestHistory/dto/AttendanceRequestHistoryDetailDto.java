package com.seulmae.seulmae.attendanceRequestHistory.dto;

import lombok.Getter;

@Getter
public class AttendanceRequestHistoryDetailDto {
    // 전달 사항
    private final String deliveryMessage;
    // 메모
    private final String attendanceRequestMemo;
    public AttendanceRequestHistoryDetailDto(String deliveryMessage,
                                             String attendanceRequestMemo) {
        this.deliveryMessage = deliveryMessage;
        this.attendanceRequestMemo = attendanceRequestMemo;
    }
}
