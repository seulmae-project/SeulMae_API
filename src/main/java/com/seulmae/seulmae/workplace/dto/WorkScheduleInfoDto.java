package com.seulmae.seulmae.workplace.dto;

import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WorkScheduleInfoDto {
    private Long workScheduleId;
    private String workScheduleTitle;
    private List<Integer> days;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;

    public WorkScheduleInfoDto(WorkSchedule workSchedule) {
        this.workScheduleId = workSchedule.getIdWorkSchedule();
        this.workScheduleTitle = workSchedule.getWorkScheduleTitle();
        this.days = makeIntegerFromWorkScheduleDay(workSchedule.getWorkScheduleDays());
        this.startTime = workSchedule.getStartTime();
        this.endTime = workSchedule.getEndTime();
        this.isActive = workSchedule.getIsActive();
    }

    private List<Integer> makeIntegerFromWorkScheduleDay(List<WorkScheduleDay> workScheduleDays) {
        return workScheduleDays.stream()
                .map(workScheduleDay -> Day.fromDay(workScheduleDay.getDay()))
                .toList();
    }
}
