package com.seulmae.seulmae.attendance.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class AttendanceRequestHistoryDto {
    // 첫 근무일
    private final LocalDateTime regDateWorkplaceApprove;
    // 급여 지급일

    // 월급 관련
    private final MonthlyWageInfo monthlyWageInfo;
    // 요청 관련
    private final Page<AttendanceRequestHistoryDetail> requestHistoryDetailDtos;

    public AttendanceRequestHistoryDto(LocalDateTime regDateWorkplaceApprove, MonthlyWageInfo monthlyWageInfo, Page<AttendanceRequestHistoryDetail> requestHistoryDetailDtos) {
        this.regDateWorkplaceApprove = regDateWorkplaceApprove;
        this.monthlyWageInfo = monthlyWageInfo;
        this.requestHistoryDetailDtos = requestHistoryDetailDtos;
    }

    @Getter
    public static class MonthlyWageInfo {
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

        public MonthlyWageInfo(Integer monthlyWage, LocalDate applyStartDate, LocalDate applyEndDate, Integer baseWage, BigDecimal monthlyWorkTime) {
            this.monthlyWage = monthlyWage;
            this.applyStartDate = applyStartDate;
            this.applyEndDate = applyEndDate;
            this.baseWage = baseWage;
            this.monthlyWorkTime = monthlyWorkTime;
        }
    }

    @Getter
    public static class AttendanceRequestHistoryDetail {
        // 날짜
        private final LocalDateTime workDate;
        // 시작 시각
        private final LocalDateTime workStartTime;
        // 종료 시각
        private final LocalDateTime workEndTime;
        // 총 일한 시간
        private final BigDecimal totalWorkTime;
        // 일급
        private final Integer wage;
        // 전달 사항
        private final String deliveryMessage;
        // 메모
        private final String attendanceRequestMemo;
        // 요청 승인 및 거절 여부
        private final Boolean isRequestApprove;
        // 미처리 상태 구분을 위한 확인 여부
        private final Boolean isManagerCheck;

        public AttendanceRequestHistoryDetail(LocalDateTime workDate,
                                              LocalDateTime workStartTime,
                                              LocalDateTime workEndTime,
                                              BigDecimal totalWorkTime,
                                              Integer wage,
                                              String deliveryMessage,
                                              String attendanceRequestMemo,
                                              Boolean isRequestApprove,
                                              Boolean isManagerCheck) {
            this.workDate = workDate;
            this.workStartTime = workStartTime;
            this.workEndTime = workEndTime;
            this.totalWorkTime = totalWorkTime;
            this.wage = wage;
            this.deliveryMessage = deliveryMessage;
            this.attendanceRequestMemo = attendanceRequestMemo;
            this.isRequestApprove = isRequestApprove;
            this.isManagerCheck = isManagerCheck;
        }
    }
}
