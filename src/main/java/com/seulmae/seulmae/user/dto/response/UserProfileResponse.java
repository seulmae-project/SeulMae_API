package com.seulmae.seulmae.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileResponse {
    private String name;
    private String userImageURL;
    private String phoneNumber;
    private String birthday;
    private List<UserWorkplaceInfoResponse> userWorkplaceInfoResponses;
}
