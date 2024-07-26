package com.seulmae.seulmae.workplace.dto;

import lombok.Getter;

@Getter
public class JoinApprovalDto {
    Long workplaceScheduleId;
    Integer payday;
    Integer baseWage;
    String memo;
}
