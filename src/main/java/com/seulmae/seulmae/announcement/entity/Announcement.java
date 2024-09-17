package com.seulmae.seulmae.announcement.entity;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "announcement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_announcement", updatable = false)
    private Long idAnnouncement;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "workplace_id", referencedColumnName = "id_workplace", nullable = false)
    private Workplace workplace;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "reg_date_announcement")
    private LocalDateTime regDateAnnouncement;

    @LastModifiedDate
    @Column(name = "revision_date_announcement")
    private LocalDateTime revisionDateAnnouncement;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer views = 0;

    @Column(name = "is_del_announcement")
    private Boolean isDelAnnouncement = false;

    @Column(name = "del_date_announcement")
    private LocalDateTime delDateAnnouncement;

    public Announcement(User user, Workplace workplace, String title, String content, Boolean isImportant) {
        this.user = user;
        this.workplace = workplace;
        this.title = title;
        this.content = content;
        this.isImportant = isImportant;
    }

    public void update(String title, String content, Boolean isImportant) {
        this.title = title;
        this.content = content;
        this.isImportant = isImportant != null ? isImportant : false;
    }

    public void delete() {
        this.isDelAnnouncement = true;
        this.delDateAnnouncement = LocalDateTime.now();
    }
    public void setImportant(Boolean important) {
        isImportant = important;
    }

    public void updateViews(Integer views) {
        this.views = views;
    }

}
