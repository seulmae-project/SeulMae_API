package com.seulmae.seulmae.user.entity;

import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user", updatable = false)
    private Long idUser;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "birthday")
    @Pattern(regexp = "\\d{8}", message = "Birthday must be exactly 8 digits")
    private String birthday;

    @Column(name = "is_male")
    private Boolean isMale;

    @Column(name = "image_url")
    private String imageURL;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_role")
    private Role authorityRole; // 권한 관련 역할 : 일반유저(USER) & 어플관리자(ADMIN)

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType; // 일반로그인인 경우, null

    @Column(name = "social_id")
    private String socialId; // 소셜 로그인 식별자 값(일반 로그인: null)

    @Column(name = "refresh_token")
    private String refreshToken;

    @CreatedDate
    @Column(name = "reg_date_user")
    private LocalDateTime regDateUser;

    @LastModifiedDate
    @Column(name = "revision_date_user")
    private LocalDateTime revisionDateUser;

    @Builder.Default
    @Column(name = "is_del_user")
    private Boolean isDelUser = false;

    @Column(name = "del_date_user")
    private LocalDateTime delDateUser;


    public User(String email, String phoneNumber, String password, String name, String birthday, Boolean isMale, String imageURL, Role role) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.isMale = isMale;
        this.imageURL = imageURL;
        this.authorityRole = role;
    }

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.authorityRole = Role.USER;
    }

    /** 정보 수정 **/
    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageURL = imageURL;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /** 암호화 **/
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    /** 복호화 **/


    /** 유효성 검사 **/

}
