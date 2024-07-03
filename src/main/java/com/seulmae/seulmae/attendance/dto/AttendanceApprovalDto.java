package com.seulmae.seulmae.attendance.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AttendanceApprovalDto {

    private Long attendanceRequestHistoryId;
    private Integer confirmedWage;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;

}
