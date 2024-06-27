package com.seulmae.seulmae.announcement.dto.request;

import lombok.Getter;

@Getter
public class AddAnnouncementRequest {
    private Long workplaceId;
    private String title;
    private String content;
    private Boolean isImportant;
}
