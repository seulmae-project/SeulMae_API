package com.seulmae.seulmae.announcement.repository;

import com.seulmae.seulmae.announcement.dto.response.AnnouncementListResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.global.support.RepositoryUnitTestSupport;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnnouncementRepositoryUnitTest extends RepositoryUnitTestSupport {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Test
    void findAnnouncementsByWorkplace() {
        User user = mockSetUpUtil.createUser("accountId", "password", "phoneNumber", "name", "19331234", true);
        Workplace workplace = mockSetUpUtil.createWorkplace("근무지", "주", "소", "031");
        Announcement announcement1 = mockSetUpUtil.createAnnouncement(user, workplace, "title1", "content1", false);
        Announcement announcement2 = mockSetUpUtil.createAnnouncement(user, workplace, "title2", "content2", false);
        announcement1.setRegDateAnnouncement(LocalDateTime.now());
        announcement2.setRegDateAnnouncement(LocalDateTime.now());

        Page<AnnouncementListResponse> announcements = announcementRepository.findAnnouncementsByWorkplace(PageRequest.of(0, 50), workplace);

        assertThat(announcements).hasSize(2)
                .extracting("title")
                .containsExactly("title1", "title2");
    }

    @Test
    void findImportantAnnouncementsByWorkplaceAndIsImportant() {
        User user = mockSetUpUtil.createUser("accountId", "password", "phoneNumber", "name", "19331234", true);
        Workplace workplace = mockSetUpUtil.createWorkplace("근무지", "주", "소", "031");
        Announcement nomalAnnouncement = mockSetUpUtil.createAnnouncement(user, workplace, "title1", "content1", false);
        Announcement importantAnnouncement = mockSetUpUtil.createAnnouncement(user, workplace, "title2", "content2", true);
        nomalAnnouncement.setRegDateAnnouncement(LocalDateTime.now());
        importantAnnouncement.setRegDateAnnouncement(LocalDateTime.now());

        List<AnnouncementMainListResponse> announcementMainListResponses = announcementRepository.findImportantAnnouncementsByWorkplaceAndIsImportant(workplace, true);

        assertThat(announcementMainListResponses).hasSize(1)
                .extracting("title")
                .containsExactly("title2");
    }
}