package com.seulmae.seulmae.notification.entity;


import com.seulmae.seulmae.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "announcement_notification")
public class AnnouncementNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_announcement_notification", updatable = false)
    private Long idAnnouncementNotification;

    @ManyToOne
    @JoinColumn(name = "to_user_id", referencedColumnName = "id_user", nullable = false)
    private User toUser;

    // [TODO] 추후 테이블 생성시 수정 필요
    @Column(name = "announcement_id")
    private Long announcementId;

    @OneToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id_notification")
    private Notification notification;

    @Column(name = "is_read")
    private Boolean isRead = false;
}
