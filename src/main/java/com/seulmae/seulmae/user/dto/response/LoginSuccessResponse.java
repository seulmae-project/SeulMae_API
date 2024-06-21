package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginSuccessResponse {
    private TokenResponse tokenResponse;
    private Role role;

}
