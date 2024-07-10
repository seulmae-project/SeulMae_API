package com.seulmae.seulmae.announcement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddAnnouncementRequest {
    private Long workplaceId;
    private String title;
    private String content;
    private Boolean isImportant;
}
