package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    private String email;
    private String password;
}
