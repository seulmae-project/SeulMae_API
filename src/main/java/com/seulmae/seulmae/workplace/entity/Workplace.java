package com.seulmae.seulmae.workplace.entity;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "workplace")
@EntityListeners(AuditingEntityListener.class)
public class Workplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workplace", updatable = false)
    private Long idWorkPlace;

    @Column(name = "workplace_code", updatable = false, unique = true)
    private String workplaceCode;

    @Column(name = "workplace_name")
    private String workplaceName;

    @Column(name = "main_address")
    private String mainAddress;

    @Column(name = "sub_address")
    private String subAddress;

    @Column(name = "tel")
    private String tel;

    @Column(name = "workplace_image_url")
    private String workplaceImageUrl;

    @CreatedDate
    @Column(name = "reg_date_workplace")
    private LocalDateTime regDateWorkplace;

    @Column(name = "is_del_user")
    private Boolean isDelWorkplace = false;

    @Column(name = "del_date_user")
    private LocalDateTime delDateWorkplace;
}
