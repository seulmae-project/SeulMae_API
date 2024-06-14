package com.seulmae.seulmae.attendance.entity;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendance")
@EntityListeners(AuditingEntityListener.class)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_attendance", updatable = false)
    private Long idAttendance;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "workplace_id", referencedColumnName = "id_workplace", nullable = false)
    private Workplace workplace;

    @Column(name = "work_date")
    private LocalDate workDate;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "total_work_time")
    private Integer totalWorkTime;

    @Column(name = "total_day_wage")
    private Integer totalDayWage;

    @Column(name = "is_agree")
    private Boolean isAgree = false;

    @CreatedDate
    @Column(name = "reg_date_attendance")
    private LocalDateTime regDateAttendance;

    @LastModifiedDate
    @Column(name = "revision_date_attendance")
    private LocalDateTime revisionDateAttendance;

    @Column(name = "is_del_attendance")
    private Boolean isDelAttendance = false;

    @Column(name = "del_date_attendance")
    private LocalDateTime delDateAttendance;
}
