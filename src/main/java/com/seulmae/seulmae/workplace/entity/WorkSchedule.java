package com.seulmae.seulmae.workplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@Table(name = "work_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_work_schedule", updatable = false)
    private Long idWorkSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    @Column(name = "work_schedule_title")
    private String workScheduleTitle;

    @OneToMany(mappedBy = "workSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkScheduleDay> workScheduleDays = new ArrayList<>();

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "reg_date_work_schedule")
    private LocalDateTime regDateWorkSchedule;

    public void setWorkScheduleDays(List<WorkScheduleDay> workScheduleDays) {
        this.workScheduleDays = workScheduleDays;
    }

}
