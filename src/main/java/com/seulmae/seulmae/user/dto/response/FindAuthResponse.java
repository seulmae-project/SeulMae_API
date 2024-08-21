package com.seulmae.seulmae.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FindAuthResponse {
    private Boolean isSuccess;
    private String accountId;

    public FindAuthResponse(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public FindAuthResponse(Boolean isSuccess, String accountId) {
        this.isSuccess = isSuccess;
        this.accountId = accountId;
    }
}


