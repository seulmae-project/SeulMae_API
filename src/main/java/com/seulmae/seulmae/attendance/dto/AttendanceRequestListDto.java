package com.seulmae.seulmae.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceRequestListDto {
    private Long attendanceRequestHistoryId;
    private String userName;
    private LocalDateTime requestDate;

    public AttendanceRequestListDto(Long attendanceRequestHistoryId, String userName, LocalDateTime requestDate) {
        this.attendanceRequestHistoryId = attendanceRequestHistoryId;
        this.userName = userName;
        this.requestDate = requestDate;
    }
}
