package com.seulmae.seulmae.workplace.dto;

import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.Getter;

import java.util.List;

@Getter
public class WorkplaceListInfoDto extends WorkplaceInfoDto{
    private String workplaceThumbnailUrl;

    public WorkplaceListInfoDto(Workplace workplace, String managerName, List<String> workplaceImageUrl, String workplaceThumbnailUrl) {
        super(workplace, managerName, workplaceImageUrl);
        this.workplaceThumbnailUrl = workplaceThumbnailUrl;
    }
}
