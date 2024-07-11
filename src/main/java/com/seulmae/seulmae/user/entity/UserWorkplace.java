package com.seulmae.seulmae.user.entity;

import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_workplace")
public class UserWorkplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_workplace", updatable = false)
    private Long idUserWorkplace;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "workplace_id", referencedColumnName = "id_workplace", nullable = false)
    private Workplace workplace;

    @Column(name = "is_manager", nullable = false)
    private Boolean isManager;

    @Column(name = "memo")
    private String memo;

    @CreatedDate
    @Column(name = "reg_date_user_workplace")
    private LocalDateTime regDateUserWorkplace;

    @LastModifiedDate
    @Column(name = "revision_date_user_workplace")
    private LocalDateTime revisionDateUserWorkplace;
}
