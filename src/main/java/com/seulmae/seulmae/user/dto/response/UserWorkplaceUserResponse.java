package com.seulmae.seulmae.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserWorkplaceUserResponse {

    private Long userId;
    private String userName;
    private String userImageUrl;
    private boolean isManager;
}
