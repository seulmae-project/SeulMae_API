package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserWorkScheduleListResponse {
    private Long userWorkScheduleId;
    private Long userId;
    private String userName;
    private String userImageURL;

    public UserWorkScheduleListResponse(UserWorkSchedule userWorkSchedule, String userImageURL) {
        this.userWorkScheduleId = userWorkSchedule.getIdUserWorkSchedule();
        this.userId = userWorkSchedule.getUser().getIdUser();
        this.userName = userWorkSchedule.getUser().getName();
        this.userImageURL = userImageURL;
    }
}
