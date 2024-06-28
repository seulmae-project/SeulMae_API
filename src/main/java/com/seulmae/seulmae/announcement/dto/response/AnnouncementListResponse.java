package com.seulmae.seulmae.announcement.dto.response;

import com.seulmae.seulmae.announcement.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Getter
public class AnnouncementListResponse {
    private Long announcementId;
    private String title;
    private String content;
    private String regDate;
    private Integer views;

    public AnnouncementListResponse(Announcement a) {
        this.announcementId = a.getIdAnnouncement();
        this.title = a.getTitle();
        this.content = a.getContent();
        this.regDate = a.getRegDateAnnouncement().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.views = a.getViews();
    }
}
