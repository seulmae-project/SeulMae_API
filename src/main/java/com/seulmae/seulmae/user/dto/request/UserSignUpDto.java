package com.seulmae.seulmae.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class UserSignUpDto {
    private String accountId;
    private String password;
    private String phoneNumber;
    private String name;
    private Boolean isMale;
    private String birthday;
}
