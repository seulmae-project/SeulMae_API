package com.seulmae.seulmae.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {
    private String name;

    public UpdateUserRequest(String name) {
        this.name = name;
    }
}
