package com.seulmae.seulmae.attendance.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AttendanceManagerMainListDto {

    private Long attendanceRequestHistoryId;
    private String userName;
    private Long userId;
    private String userImageUrl;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private BigDecimal totalWorkTime;
    private Boolean isRequestApprove;
    private Boolean isManagerCheck;

    public AttendanceManagerMainListDto(Long attendanceRequestHistoryId, String userName, Long userId, LocalDateTime workStartTime, LocalDateTime workEndTime, BigDecimal totalWorkTime, Boolean isRequestApprove, Boolean isManagerCheck) {
        this.attendanceRequestHistoryId = attendanceRequestHistoryId;
        this.userName = userName;
        this.userId = userId;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.totalWorkTime = totalWorkTime;
        this.isRequestApprove = isRequestApprove;
        this.isManagerCheck = isManagerCheck;
    }
    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
