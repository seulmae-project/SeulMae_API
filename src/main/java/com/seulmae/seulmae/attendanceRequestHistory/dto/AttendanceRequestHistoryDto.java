package com.seulmae.seulmae.attendanceRequestHistory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRequestHistoryDto {
    // 날짜
    private final LocalDate workDate;
    // 시작 시각
    private final LocalDateTime workStartTime;
    // 종료 시각
    private final LocalDateTime workEndTime;
    // 총 일한 시간
    private final BigDecimal totalWorkTime;
    // 일급
    private final Integer wage;
    // 요청 승인 및 거절 여부
    private final Boolean isRequestApprove;
    // 미처리 상태 구분을 위한 확인 여부
    private final Boolean isManagerCheck;

    private final Long idAttendanceRequestHistory;

    public AttendanceRequestHistoryDto(LocalDate workDate,
                                       LocalDateTime workStartTime,
                                       LocalDateTime workEndTime,
                                       BigDecimal totalWorkTime,
                                       Integer wage,
                                       Boolean isRequestApprove,
                                       Boolean isManagerCheck,
                                       Long idAttendanceRequestHistory) {
        this.workDate = workDate;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.totalWorkTime = totalWorkTime;
        this.wage = wage;
        this.isRequestApprove = isRequestApprove;
        this.isManagerCheck = isManagerCheck;
        this.idAttendanceRequestHistory = idAttendanceRequestHistory;
    }
}
