package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.workplace.vo.AddressVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserWorkplaceInfoResponse {
    private Long workplaceId;
    private String workplaceName;
    private AddressVo address;
    private String workplaceTel;
    private List<String> workplaceImageUrlList;
    private String managerName;
    private Boolean isManager;

}
