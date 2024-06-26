package com.seulmae.seulmae.notification.entity;

import com.seulmae.seulmae.notification.NotificationType;
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

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    @CreatedDate
    @Column(name = "reg_date_notification")
    private LocalDateTime regDateNotification;

    @Builder
    public Notification(String title, String message, NotificationType notificationType) {
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }
}
