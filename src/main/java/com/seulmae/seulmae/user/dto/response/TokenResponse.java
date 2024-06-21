package com.seulmae.seulmae.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public TokenResponse(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public TokenResponse(String accessToken, String refreshToken, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }
}
