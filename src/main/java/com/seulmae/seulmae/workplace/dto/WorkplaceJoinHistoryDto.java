package com.seulmae.seulmae.workplace.dto;


import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WorkplaceJoinHistoryDto {
    private Workplace workplace;
    private Boolean isApprove;
    private LocalDateTime decisionDate;
    private LocalDateTime regDateWorkplaceJoinHistory;
    private String stringDecisionDate;
    private String stringRegDateWorkplaceJoinHistory;

    public WorkplaceJoinHistoryDto(Workplace workplace, Boolean isApprove, LocalDateTime decisionDate, LocalDateTime regDateWorkplaceJoinHistory) {
        this.workplace = workplace;
        this.isApprove = isApprove;
        this.decisionDate = decisionDate;
        this.regDateWorkplaceJoinHistory = regDateWorkplaceJoinHistory;
    }

    public void setStringDecisionDate(String stringDecisionDate) {
        this.stringDecisionDate = stringDecisionDate;
    }

    public void setStringRegDateWorkplaceJoinHistory(String stringRegDateWorkplaceJoinHistory) {
        this.stringRegDateWorkplaceJoinHistory = stringRegDateWorkplaceJoinHistory;
    }
}
