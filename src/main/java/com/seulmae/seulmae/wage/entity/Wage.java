package com.seulmae.seulmae.wage.entity;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wage")
@EntityListeners(AuditingEntityListener.class)
public class Wage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_wage", updatable = false)
    private Long idWage;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "workplace_id", referencedColumnName = "id_workplace", nullable = false)
    private Workplace workplace;

//    @OneToOne
//    @JoinColumn(name = "wage_info_id", referencedColumnName = "id_wage_info")
//    private WageInfo wageInfo;

    @Column(name = "base_wage")
    private Integer baseWage;

    @Column(name = "payday")
    private Integer payday;

    @Column(name = "apply_start_date")
    private LocalDate applyStartDate;

    @Column(name = "apply_end_date")
    private LocalDate applyEndDate;
}
