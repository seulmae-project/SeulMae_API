package com.seulmae.seulmae.workplace.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkScheduleUpdateDto {
    private String workScheduleTitle;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Integer> days;
    private Boolean isActive;
}
