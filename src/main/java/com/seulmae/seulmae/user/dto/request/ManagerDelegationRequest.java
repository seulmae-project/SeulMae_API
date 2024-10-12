package com.seulmae.seulmae.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerDelegationRequest {
    private Long userId;
    private Long workplaceId;
}
