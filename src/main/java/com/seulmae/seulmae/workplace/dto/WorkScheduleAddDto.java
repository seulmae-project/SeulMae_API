package com.seulmae.seulmae.workplace.dto;

import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
public class WorkScheduleAddDto {
    private Long workplaceId;
    private String workScheduleTitle;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Integer> days;
}
