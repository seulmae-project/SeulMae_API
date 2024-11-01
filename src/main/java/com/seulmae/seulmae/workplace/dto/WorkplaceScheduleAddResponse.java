package com.seulmae.seulmae.workplace.dto;

import com.seulmae.seulmae.global.util.DateFormatUtil;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WorkplaceScheduleAddResponse {
    private Long idWorkSchedule;
    private Long workPlaceId;
    private String workScheduleTitle;
    private List<WorkplaceScheduleDaysAddResponse> workScheduleDays;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;
    private String regDateWorkSchedule;

    public WorkplaceScheduleAddResponse(WorkSchedule workSchedule) {
        this.idWorkSchedule = workSchedule.getIdWorkSchedule();
        this.workPlaceId = workSchedule.getWorkplace().getIdWorkPlace();
        this.workScheduleTitle = workSchedule.getWorkScheduleTitle();
        this.workScheduleDays = workSchedule.getWorkScheduleDays().stream()
                .map(WorkplaceScheduleDaysAddResponse::new).collect(Collectors.toList());
        this.startTime = workSchedule.getStartTime();
        this.endTime = workSchedule.getEndTime();
        this.isActive = workSchedule.getIsActive();
        this.regDateWorkSchedule = DateFormatUtil.formatToDateTimeString(workSchedule.getRegDateWorkSchedule());
    }
}
