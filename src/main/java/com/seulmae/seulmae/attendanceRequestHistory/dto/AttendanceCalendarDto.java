package com.seulmae.seulmae.attendanceRequestHistory.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AttendanceCalendarDto {
    private final LocalDate workDate;
    private final Boolean isRequestApprove;
    private final Boolean isManagerCheck;
    private final Long idAttendanceRequestHistory;

    public AttendanceCalendarDto(LocalDate workDate, Boolean isRequestApprove, Boolean isManagerCheck, Long idAttendanceRequestHistory) {
        this.workDate = workDate;
        this.isRequestApprove = isRequestApprove;
        this.isManagerCheck = isManagerCheck;
        this.idAttendanceRequestHistory = idAttendanceRequestHistory;
    }
}
