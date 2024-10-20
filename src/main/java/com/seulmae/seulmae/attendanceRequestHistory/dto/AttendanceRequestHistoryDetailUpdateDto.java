package com.seulmae.seulmae.attendanceRequestHistory.dto;

import lombok.Getter;

@Getter
public class AttendanceRequestHistoryDetailUpdateDto {
    private final Long idAttendanceRequestHistory;
    // 메모
    private final String attendanceRequestMemo;

    public AttendanceRequestHistoryDetailUpdateDto(Long idAttendanceRequestHistory, String attendanceRequestMemo) {
        this.idAttendanceRequestHistory = idAttendanceRequestHistory;
        this.attendanceRequestMemo = attendanceRequestMemo;
    }
}
