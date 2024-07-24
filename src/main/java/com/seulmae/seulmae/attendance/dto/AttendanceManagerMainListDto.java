package com.seulmae.seulmae.attendance.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AttendanceManagerMainListDto {

    private String userName;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private BigDecimal totalWorkTime;
    private Boolean isRequestApprove;
    private Boolean isManagerCheck;

    public AttendanceManagerMainListDto(String userName, LocalDateTime workStartTime, LocalDateTime workEndTime, BigDecimal totalWorkTime, Boolean isRequestApprove, Boolean isManagerCheck) {
        this.userName = userName;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.totalWorkTime = totalWorkTime;
        this.isRequestApprove = isRequestApprove;
        this.isManagerCheck = isManagerCheck;
    }
}
