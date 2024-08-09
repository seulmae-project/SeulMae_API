package com.seulmae.seulmae.attendanceRequestHistory.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class MonthlyWorkSummaryDto {
    // 승인 된 월 지급 합계
    private final Integer monthlyWage;
    // 시작 일
    private final LocalDate applyStartDate;
    // 종료 일
    private final LocalDate applyEndDate;
    // 시급
    private final Integer baseWage;
    // 총 일한 시간(월)
    private final BigDecimal monthlyWorkTime;

    public MonthlyWorkSummaryDto(Integer monthlyWage, LocalDate applyStartDate, LocalDate applyEndDate, Integer baseWage, BigDecimal monthlyWorkTime) {
        this.monthlyWage = monthlyWage;
        this.applyStartDate = applyStartDate;
        this.applyEndDate = applyEndDate;
        this.baseWage = baseWage;
        this.monthlyWorkTime = monthlyWorkTime;
    }
}
