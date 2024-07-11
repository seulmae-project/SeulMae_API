package com.seulmae.seulmae.notification.entity;

import com.seulmae.seulmae.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "fcm_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fcm_token", updatable = false)
    private Long idFcmToken;

    @Column(name = "fcm_token", updatable = false)
    private String fcmToken;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user")
    private User user;

    @CreatedDate
    @Column(name = "reg_date_fcm_token")
    private LocalDateTime regDateFcmToken;

    @LastModifiedDate
    @Column(name = "revision_date_fcm_token")
    private LocalDateTime revsionDateFcmToken;
}
