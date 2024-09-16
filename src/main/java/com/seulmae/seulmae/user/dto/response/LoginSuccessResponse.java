package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginSuccessResponse {
    private TokenResponse tokenResponse;
    private Role role;
    private List<WorkplaceResponse> workplaceResponses;

}
