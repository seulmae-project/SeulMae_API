package com.seulmae.seulmae.workplace.entity;

import com.seulmae.seulmae.workplace.Day;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@Table(name = "work_schedule_day")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkScheduleDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_work_schedule_day", updatable = false)
    private Long idWorkScheduleDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_schedule_id")
    private WorkSchedule workSchedule;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "week_day")
    private Day day;

    @Override
    public String toString() {
        return "WorkScheduleDay{" +
                "idWorkScheduleDay=" + idWorkScheduleDay +
                ", workSchedule=" + workSchedule +
                ", day=" + day +
                '}';
    }


}
