package com.seulmae.seulmae.attendance.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class GetOffWorkDto {

    private Long workplaceId;
    private LocalDate workDate;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private Integer unconfirmedWage;
    private BigDecimal totalWorkTime;
}
