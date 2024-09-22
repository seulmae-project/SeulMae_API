package com.seulmae.seulmae.workplace.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinApprovalDto {
    Long workplaceScheduleId;
    Integer payday;
    Integer baseWage;
    String memo;
}
