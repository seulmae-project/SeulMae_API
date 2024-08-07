package com.seulmae.seulmae.workplace.entity;

import com.seulmae.seulmae.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "workplace_approve")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkplaceApprove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workplace_approve", updatable = false)
    private Long idWorkPlaceApprove;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Workplace workplace;

    @Column(name = "workplace_join_history_id")
    private Long workplaceJoinHistoryId;

    @CreatedDate
    @Column(name = "reg_date_workplace_approve")
    private LocalDateTime regDateWorkplaceApprove;
}
