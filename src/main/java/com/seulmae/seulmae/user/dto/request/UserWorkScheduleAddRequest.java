package com.seulmae.seulmae.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserWorkScheduleAddRequest {
    private Long targetUserId;
    private Long workScheduleId;
}
