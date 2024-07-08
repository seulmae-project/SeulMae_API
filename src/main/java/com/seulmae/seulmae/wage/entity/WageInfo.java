package com.seulmae.seulmae.wage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wage_info")
@EntityListeners(AuditingEntityListener.class)
public class WageInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_wage_info", updatable = false)
    private Long idWageInfo;

    @Column(name = "base_wage")
    private Integer baseWage;

//    @Column(name = "work_start_time")
//    private LocalTime workStartTime;
//
//    @Column(name = "work_end_time")
//    private LocalTime workEndTime;
//
//    @Column(name = "default_time")
//    private Integer defaultTime;
//
//    @Column(name = "is_holiday_pay")
//    private Boolean isHolidayPay = false;
//
//    @Column(name = "overtime_rate")
//    private Double overtimeRate;
//
//    @Column(name = "overtime_start_time")
//    private LocalTime overtimeStartTime;


}
