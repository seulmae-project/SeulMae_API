package com.seulmae.seulmae.workplace.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkplaceAddDto {

    private String workplaceName;
    private String mainAddress;
    private String subAddress;
    private String workplaceTel;

    public WorkplaceAddDto(String workplaceName, String mainAddress, String subAddress, String workplaceTel) {
        this.workplaceName = workplaceName;
        this.mainAddress = mainAddress;
        this.subAddress = subAddress;
        this.workplaceTel = workplaceTel;
    }
}
