package com.seulmae.seulmae.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceApprovalDto {

    private Long attendanceRequestHistoryId;
    private Integer confirmedWage;
    private LocalDateTime changedWorkStartTime;
    private LocalDateTime changedWorkEndTime;

}
