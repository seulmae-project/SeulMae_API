package com.seulmae.seulmae.user.entity;

import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.exception.MatchPasswordException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class User implements UserDetails {
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

    //[TODO] MULTIPART 구현
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserImage userImage;

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


    public User(String email, String phoneNumber, String password, String name, String birthday, Boolean isMale, UserImage userImage, Role role) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.isMale = isMale;
        this.userImage = userImage;
        this.authorityRole = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", isMale=" + isMale +
                ", userImage='" + userImage + '\'' +
                ", authorityRole=" + authorityRole +
                ", socialType=" + socialType +
                ", socialId='" + socialId + '\'' +
                '}';
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

    public void updateUserImage(UserImage userImage) {
        this.userImage = userImage;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public void updateAdditionalInfo(String name, Boolean isMale, String birthday) {
        this.name = name;
        this.isMale = isMale;
        this.birthday = birthday;
    }

    /** 회원 탈퇴 **/
    public void deleteUser() {
        this.isDelUser = true;
        this.delDateUser = LocalDateTime.now();
    }

    /** 암호화 **/
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    /**
     * 기존 비밀번호와 새 비밀번호가 일치하는지 확인하는 메서드
     */
    private boolean isSamePassword(PasswordEncoder passwordEncoder, String rawPassword) {
        return passwordEncoder.matches(rawPassword, this.password);
    }


    /** 비밀번호 변경 **/
    public void changePassword(PasswordEncoder passwordEncoder, String password) {
        if (isSamePassword(passwordEncoder, password)) {
            throw new MatchPasswordException("기존 비밀번호와 일치합니다. 다른 비밀번호를 입력해주세요.");
        }
        updatePassword(passwordEncoder, password);
    }

    /** 유효성 검사 **/


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
