package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;

@Getter
public class OAuth2AdditionalDataRequest {
    private String name;
    private Boolean isMale;
    private String birthday;


    public OAuth2AdditionalDataRequest(String name, Boolean isMale, String birthday) {
        this.name = name;
        this.isMale = isMale;
        this.birthday = birthday;
    }
}
