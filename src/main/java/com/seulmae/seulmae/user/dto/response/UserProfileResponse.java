package com.seulmae.seulmae.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {
    private String name;
    private String userImageURL;

    public UserProfileResponse(String name, String userImageURL) {
        this.name = name;
        this.userImageURL = userImageURL;
    }
}
