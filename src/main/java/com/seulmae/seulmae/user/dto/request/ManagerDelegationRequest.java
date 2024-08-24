package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class ManagerDelegationRequest {
    private Long userId;
    private Long workplaceId;
}
