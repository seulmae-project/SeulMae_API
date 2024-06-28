package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.workplace.vo.AddressVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserWorkplaceInfoResponse {
    private String workplaceName;
    private AddressVo address;
}
