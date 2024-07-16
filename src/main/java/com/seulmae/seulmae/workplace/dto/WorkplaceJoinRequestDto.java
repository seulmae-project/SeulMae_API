package com.seulmae.seulmae.workplace.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkplaceJoinRequestDto {

    private Long workplaceApproveId;
    private String userName;
    private LocalDateTime requestDate;

    public WorkplaceJoinRequestDto(Long workplaceApproveId, String userName, LocalDateTime requestDate) {
        this.workplaceApproveId = workplaceApproveId;
        this.userName = userName;
        this.requestDate = requestDate;
    }
}
