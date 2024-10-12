package com.seulmae.seulmae.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceRequestDto {

    private Long workplaceId;
    private LocalDate workDate;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private Integer confirmedWage;
    private Integer unconfirmedWage;
    private BigDecimal totalWorkTime;
    private String deliveryMessage;
    private Integer day;
}
