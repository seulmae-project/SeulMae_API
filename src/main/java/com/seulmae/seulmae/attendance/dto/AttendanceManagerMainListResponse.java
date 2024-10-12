package com.seulmae.seulmae.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AttendanceManagerMainListResponse {
    private Long attendanceRequestHistoryId;
    private String userName;
    private Long userId;
    private String userImageUrl;
    private String workStartTime;
    private String workEndTime;
    private String changedWorkStartTime;
    private String changedWorkEndTime;
    private BigDecimal totalWorkTime;
    private Boolean isRequestApprove;
    private Boolean isManagerCheck;
}
