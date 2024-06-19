package com.seulmae.seulmae.workplace.dto;

import lombok.Getter;

@Getter
public class WorkplaceModifyDto {
    private Long workplaceId;
    private String workplaceName;
    private String mainAddress;
    private String subAddress;
    private String workplaceTel;
}
