package com.seulmae.seulmae.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_image")
@EntityListeners(AuditingEntityListener.class)
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_image", updatable = false)
    private Long idUserImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_image_name")
    private String userImageName;

    @Column(name = "user_image_path")
    private String userImagePath;

    @Column(name = "user_image_extension")
    private String userImageExtension;

    @CreatedDate
    @Column(name = "reg_date_user_image")
    private LocalDateTime regDateUserImage;

//    @LastModifiedDate
//    @Column(name = "revision_date_user_image")
//    private LocalDateTime revisionDateUserImage;
//
//    @Column(name = "is_del_user_image")
//    private Boolean isDelUserImage = false;
//
//    @Column(name = "del_date_user_image")
//    private LocalDateTime delDateUserImage;

    public UserImage(User user, String userImageName, String userImagePath, String userImageExtension) {
        this.user = user;
        this.userImageName = userImageName;
        this.userImagePath = userImagePath;
        this.userImageExtension = userImageExtension;
    }

    public void update(String userImageName, String userImagePath, String userImageExtension) {
        this.userImageName = userImageName;
        this.userImagePath = userImagePath;
        this.userImageExtension = userImageExtension;
    }

//    public void delete() {
//        this.isDelUserImage = true;
//        this.delDateUserImage = LocalDateTime.now();
//    }


}
