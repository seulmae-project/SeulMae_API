package com.seulmae.seulmae.workplace.entity;

import com.seulmae.seulmae.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "workplace_join_history")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkplaceJoinHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workplace_join_history", updatable = false)
    private Long idWorkplaceJoinHistory;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "workplace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Workplace workplace;

    @Column(name = "is_approve")
    private Boolean isApprove;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @CreatedDate
    @Column(name = "reg_date_workplace_join_history")
    private LocalDateTime regDateWorkplaceJoinHistory;

    public void setIsApproveTrue() {
        this.isApprove = true;
        this.decisionDate = LocalDateTime.now();
    }
    public void setIsApproveFalse() {
        this.isApprove = false;
        this.decisionDate = LocalDateTime.now();
    }
}
