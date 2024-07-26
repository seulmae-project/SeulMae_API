package com.seulmae.seulmae.attendanceRequestHistory.entity;

import com.seulmae.seulmae.attendance.entity.Attendance;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "attendance_request_history")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceRequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_attendance_request_history", updatable = false)
    private Long idAttendanceRequestHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private com.seulmae.seulmae.attendance.entity.Attendance attendance;

    @Column(name = "work_start_time")
    private LocalDateTime workStartTime;

    @Column(name = "work_end_time")
    private LocalDateTime workEndTime;

    @Column(name = "total_work_time")
    private BigDecimal totalWorkTime = BigDecimal.ZERO;

    @Column(name = "is_request_approve")
    private Boolean isRequestApprove; /** 요청 승인 및 거절 여부 **/

    @Column(name = "is_manager_check")
    private Boolean isManagerCheck; /** 미처리 상태 구분을 위한 확인 여부 **/

    @Column(name = "delivery_message")
    private String deliveryMessage;

    @Column(name = "attendance_request_memo")
    private String attendanceRequestMemo;

    @LastModifiedDate
    @Column(name = "check_date")
    private LocalDateTime checkDate;

    @CreatedDate
    @Column(name = "reg_date_attendance_request_history")
    private LocalDateTime regDateAttendanceRequestHistory;

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
    public void setIsManagerCheckTrueAndCheckDate() {
        this.isManagerCheck = true;
        this.checkDate = LocalDateTime.now();
    }

    public void setIsRequestApproveTrue() {
        this.isRequestApprove = true;
    }

}
