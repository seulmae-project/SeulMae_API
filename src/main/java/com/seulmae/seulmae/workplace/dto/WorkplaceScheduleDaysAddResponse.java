package com.seulmae.seulmae.workplace.dto;

import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import lombok.Getter;

@Getter
public class WorkplaceScheduleDaysAddResponse {
    private Long idWorkScheduleDay;
    private Integer day;

    public WorkplaceScheduleDaysAddResponse(WorkScheduleDay workScheduleDay) {
        this.idWorkScheduleDay = workScheduleDay.getIdWorkScheduleDay();
        this.day = workScheduleDay.getDay().ordinal();
    }
}
