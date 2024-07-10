package com.seulmae.seulmae.workplace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleAddDto {
    private Long workplaceId;
    private String workScheduleTitle;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Integer> days;
}
