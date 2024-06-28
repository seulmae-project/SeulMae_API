package com.seulmae.seulmae.announcement.repository;

import com.seulmae.seulmae.announcement.dto.response.AnnouncementListResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT new com.seulmae.seulmae.announcement.dto.response.AnnouncementListResponse(a) " +
            "FROM Announcement a " +
            "WHERE a.workplace = :workplace " +
            "AND a.isDelAnnouncement != TRUE " +
            "ORDER BY a.regDateAnnouncement DESC")
    Page<AnnouncementListResponse> findAnnouncementsByWorkplace(Pageable of, Workplace workplace);


    @Query("SELECT new com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse(a.idAnnouncement, a.title) " +
            "FROM Announcement a " +
            "WHERE a.isImportant = :isImportant " +
            "AND a.workplace = :workplace " +
            "AND a.isDelAnnouncement != TRUE " +
            "ORDER BY a.regDateAnnouncement DESC")
    List<AnnouncementMainListResponse> findImportantAnnouncementsByWorkplaceAndIsImportant(Workplace workplace, boolean isImportant);

    @Query("SELECT new com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse(a.idAnnouncement, a.title) " +
            "FROM Announcement a " +
            "WHERE a.workplace = :workplace " +
            "AND a.isDelAnnouncement != TRUE " +
            "ORDER BY a.regDateAnnouncement DESC")
    List<AnnouncementMainListResponse> findMainAnnouncementsByWorkplace(Workplace workplace);



}
