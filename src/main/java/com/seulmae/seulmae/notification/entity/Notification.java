package com.seulmae.seulmae.notification.entity;

import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification", updatable = false)
    private Long idNotification;

    @ManyToOne
    @JoinColumn(name = "use_workplace_id", referencedColumnName = "id_user_workplace", nullable = false)
    private UserWorkplace userWorkplace;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    @Column(name = "linked_id")
    private Long linkedId;

    @CreatedDate
    @Column(name = "reg_date_notification")
    private LocalDateTime regDateNotification;

    @Builder
    public Notification(String title, String message, NotificationType notificationType, Long linkedId, UserWorkplace userWorkplace) {
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.linkedId = linkedId;
        this.userWorkplace = userWorkplace;
    }
}
