package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpDto {
    private String email;
    private String password;
    private String phoneNumber;
    private String name;
    private String imageUrl;
    private Boolean isMale;
    private String birthday;
}
