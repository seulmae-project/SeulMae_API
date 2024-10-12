package com.seulmae.seulmae.workplace.dto;

import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.Getter;

import java.util.List;

@Getter
public class WorkplaceInfoDto {
    private Long workplaceId;
    private String workplaceCode;
    private String workplaceName;
    private String workplaceTel;
    private String MainAddress;
    private String SubAddress;
    private List<String> workplaceImageUrlList;

    public WorkplaceInfoDto(Workplace workplace, List<String> workplaceImageUrlList) {
        this.workplaceId = workplace.getIdWorkPlace();
        this.workplaceCode = workplace.getWorkplaceCode();
        this.workplaceName = workplace.getWorkplaceName();
        this.workplaceTel = workplace.getWorkplaceTel();
        this.MainAddress = workplace.getAddressVo().getMainAddress();
        this.SubAddress = workplace.getAddressVo().getSubAddress();
        this.workplaceImageUrlList = workplaceImageUrlList;
    }
}
