package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class OAuth2AdditionalDataRequest {
    private String name;
    private String imageURL;
    private Boolean isMale;
    private String birthday;
}
