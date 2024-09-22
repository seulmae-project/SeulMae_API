package com.seulmae.seulmae.attendanceRequestHistory.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AttendanceCalendarDto {
    private final Long userId; // 임시로 userId 반환, 이후 어떤 정보 보낼 지 논의 필요
    private final LocalDate workDate;
    private final Boolean isRequestApprove;
    private final Boolean isManagerCheck;
    private final Long idAttendanceRequestHistory;

    public AttendanceCalendarDto(Long userId, LocalDate workDate, Boolean isRequestApprove, Boolean isManagerCheck, Long idAttendanceRequestHistory) {
        this.userId = userId;
        this.workDate = workDate;
        this.isRequestApprove = isRequestApprove;
        this.isManagerCheck = isManagerCheck;
        this.idAttendanceRequestHistory = idAttendanceRequestHistory;
    }
}
