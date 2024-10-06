package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.user.entity.UserWorkplace;
import lombok.Getter;

@Getter
public class WorkplaceResponse {
    private Long workplaceId;
    private String workplaceName;
    private Long userWorkplaceId;
    private Boolean isManager;

    public WorkplaceResponse(UserWorkplace userWorkplace) {
        this.workplaceId = userWorkplace.getWorkplace().getIdWorkPlace();
        this.workplaceName = userWorkplace.getWorkplace().getWorkplaceName();
        this.userWorkplaceId = userWorkplace.getIdUserWorkplace();
        this.isManager = userWorkplace.getIsManager();
    }
}
