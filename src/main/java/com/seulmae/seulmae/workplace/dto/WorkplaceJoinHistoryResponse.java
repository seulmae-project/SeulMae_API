package com.seulmae.seulmae.workplace.dto;

import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkplaceJoinHistoryResponse {
    private Long workplaceId;
    private String workplaceName;
    private Boolean isApprove;
    private String decisionDate;
    private String regDateWorkplaceJoinHistory;
}
