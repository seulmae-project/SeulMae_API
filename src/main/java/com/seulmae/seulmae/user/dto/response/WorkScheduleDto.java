package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import lombok.Getter;

import java.util.List;

@Getter
public class WorkScheduleDto {
    private Long workScheduleId;
    private String workScheduleTitle;
    private List<Integer> days;

    public WorkScheduleDto(UserWorkSchedule userWorkSchedule) {
        this.workScheduleId = userWorkSchedule.getWorkSchedule().getIdWorkSchedule();
        this.workScheduleTitle = userWorkSchedule.getWorkSchedule().getWorkScheduleTitle();
        this.days = makeIntegerFromWorkScheduleDay(userWorkSchedule.getWorkSchedule().getWorkScheduleDays());
    }

    private List<Integer> makeIntegerFromWorkScheduleDay(List<WorkScheduleDay> workScheduleDays) {
        return workScheduleDays.stream()
                .map(workScheduleDay -> Day.fromDay(workScheduleDay.getDay()))
                .toList();
    }
    public static List<WorkScheduleDto> createWorkScheduleDtoList(List<UserWorkSchedule> userWorkScheduleList) {
        return userWorkScheduleList.stream()
                .map(userWorkSchedule -> new WorkScheduleDto(userWorkSchedule))
                .toList();
    }
}
