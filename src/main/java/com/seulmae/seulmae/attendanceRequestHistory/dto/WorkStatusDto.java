package com.seulmae.seulmae.attendanceRequestHistory.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WorkStatusDto {
    // 총 근무 일
    private final long workedDays;
    // 첫 근무일
    private final LocalDate firstWorkDate;
    // 급여 지급일
    public final Integer payday;

    public WorkStatusDto(long workedDays, LocalDate firstWorkDate, Integer payday) {
        this.workedDays = workedDays;
        this.firstWorkDate = firstWorkDate;
        this.payday = payday;
    }
}
