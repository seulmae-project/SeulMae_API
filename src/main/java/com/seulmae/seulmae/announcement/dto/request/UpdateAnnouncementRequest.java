package com.seulmae.seulmae.announcement.dto.request;

import lombok.Getter;

@Getter
public class UpdateAnnouncementRequest {
    private String title;
    private String content;
    private Boolean isImportant;
}
