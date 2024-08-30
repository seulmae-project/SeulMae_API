package com.seulmae.seulmae.workplace.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinApprovalDto {
    Long workplaceScheduleId;
    Integer payday;
    Integer baseWage;
    String memo;
}
