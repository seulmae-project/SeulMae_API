package com.seulmae.seulmae.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seulmae.seulmae.global.config.oauth2.userInfo.OAuth2UserInfo;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
public class KaKaoUserInfo {
    @JsonProperty("id")
    private String socialId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    public static class KakaoAccount {

//        @JsonProperty("email")
//        private String email;

        @JsonProperty("profile")
        private Profile profile;

        @Getter
        public static class Profile {

//            @JsonProperty("nickname")
//            private String nickname;

//            @JsonProperty("profile_image_url")
//            private String profileImageUrl;

            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;
        }
    }

    public User toEntity() {
        return User.builder()
                .socialType(SocialType.KAKAO)
                .socialId(socialId)
                .accountId(socialId)
                .authorityRole(Role.GUEST)
                .build();
    }
}
