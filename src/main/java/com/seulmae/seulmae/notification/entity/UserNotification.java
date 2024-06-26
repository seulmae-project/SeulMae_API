package com.seulmae.seulmae.notification.entity;

import com.seulmae.seulmae.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "user_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_notification", updatable = false)
    private Long idUserNotification;

    @ManyToOne
    @JoinColumn(name = "from_user_id", referencedColumnName = "id_user", nullable = false)
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", referencedColumnName = "id_user", nullable = false)
    private User toUser;

    // [TODO] 여기 조금도 고민해봐야함....알림 늘어 날 때마다, 계속 테이블을 만들수는 없다.
    // 그리고 추가적으로 수정 필요
    @Column(name = "attendance_request_history_id")
    private Long attendanceRequestHistoryId;

    @OneToOne
    @JoinColumn(name = "notification_id", referencedColumnName = "id_notification")
    private Notification notification;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Builder
    public UserNotification(User fromUser, User toUser, Long attendanceRequestHistoryId, Notification notification, Boolean isRead) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.attendanceRequestHistoryId = attendanceRequestHistoryId;
        this.notification = notification;
        this.isRead = isRead;
    }
}
