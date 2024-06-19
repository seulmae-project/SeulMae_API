package com.seulmae.seulmae.workplace.dto;

import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.Getter;

import java.util.List;

@Getter
public class WorkplaceListInfoDto extends WorkplaceInfoDto{
    private String workplaceManagerName;
    private String workplaceThumbnailUrl;

    public WorkplaceListInfoDto(Workplace workplace, List<String> workplaceImageUrl, String workplaceManagerName, String workplaceThumbnailUrl) {
        super(workplace, workplaceImageUrl);
        this.workplaceManagerName = workplaceManagerName;
        this.workplaceThumbnailUrl = workplaceThumbnailUrl;
    }
}
