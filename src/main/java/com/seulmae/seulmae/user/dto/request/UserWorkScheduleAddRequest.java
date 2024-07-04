package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class UserWorkScheduleAddRequest {
    private Long targetUserId;
    private Long workScheduleId;
}
