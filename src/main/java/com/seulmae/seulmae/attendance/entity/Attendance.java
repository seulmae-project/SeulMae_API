package com.seulmae.seulmae.attendance.entity;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "attendance")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "confirmed_wage")
    private Integer confirmedWage;

    @Column(name = "unconfirmed_wage")
    private Integer unconfirmedWage;

    public void setUnconfirmedWage(Integer unconfirmedWage) {
        this.unconfirmedWage = unconfirmedWage;
    }

    public void setConfirmedWage(Integer confirmedWage) {
        this.confirmedWage = confirmedWage;
    }
}
