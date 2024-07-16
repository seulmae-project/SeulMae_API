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
import java.util.Objects;

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
    private LocalDateTime revisionDateFcmToken;

    public FcmToken(String fcmToken, User user) {
        this.fcmToken = fcmToken;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FcmToken fcmToken1 = (FcmToken) o;
        return Objects.equals(fcmToken, fcmToken1.fcmToken) && Objects.equals(user, fcmToken1.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fcmToken, user);
    }

    @Override
    public String toString() {
        return "FcmToken{" +
                "idFcmToken=" + idFcmToken +
                ", fcmToken='" + fcmToken + '\'' +
                ", user=" + user.getName() +
                ", regDateFcmToken=" + regDateFcmToken +
                ", revisionDateFcmToken=" + revisionDateFcmToken +
                '}';
    }
}
