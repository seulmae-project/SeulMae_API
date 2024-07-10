package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordRequest {
    private String accountId;
    private String password;

    public ChangePasswordRequest(String accountId, String password) {
        this.accountId = accountId;
        this.password = password;
    }
}
