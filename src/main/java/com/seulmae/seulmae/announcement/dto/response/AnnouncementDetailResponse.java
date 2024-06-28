package com.seulmae.seulmae.announcement.dto.response;

import com.seulmae.seulmae.announcement.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Getter
public class AnnouncementDetailResponse {
    private Long workplaceId;
    private String title;
    private String content;
    private String regDate;
    private String revisionDate;
    private Integer views;

    public AnnouncementDetailResponse(Announcement a) {
        this.workplaceId = a.getWorkplace().getIdWorkPlace();
        this.title = a.getTitle();
        this.content = a.getContent();
        this.regDate = a.getRegDateAnnouncement().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.revisionDate = a.getRevisionDateAnnouncement().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.views = a.getViews();
    }

}
