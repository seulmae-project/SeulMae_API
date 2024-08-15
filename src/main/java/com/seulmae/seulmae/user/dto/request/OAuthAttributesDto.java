package com.seulmae.seulmae.user.dto.request;

import com.seulmae.seulmae.global.config.oauth2.userInfo.AppleOAuth2UserInfo;
import com.seulmae.seulmae.global.config.oauth2.userInfo.KakaoOAuth2UserInfo;
import com.seulmae.seulmae.global.config.oauth2.userInfo.OAuth2UserInfo;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/**
 * 추후 소셜 종류가 추가될 경우, 소셜별로 데이터를 받는 객체를 분기처리하는 DTO 클래스
 */
@Getter
public class OAuthAttributesDto {
    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값
    private OAuth2UserInfo oAuth2UserInfo; // 소셜 타입별 로그인 유저 정보


    @Builder
    public OAuthAttributesDto(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    /**
     * SocialType에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
     * 파라미터 : userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값 / attributes : OAuth 서비스의 유저 정보들
     * 소셜별 of 메소드(ofGoogle, ofKaKao, ofNaver)들은 각각 소셜 로그인 API에서 제공하는
     * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후 build
     */
    public static OAuthAttributesDto of(SocialType socialType, String usernameAttributeName, Map<String, Object> attributes) {
        if (socialType == SocialType.KAKAO) {
            return ofKakao(usernameAttributeName, attributes);
        } else if (socialType == SocialType.APPLE) {
            return ofApple(usernameAttributeName, attributes);
        }
        return null; // 추후 소셜로그인 추가된다면 추가
    }

    private static OAuthAttributesDto ofKakao(String usernameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributesDto.builder()
                .nameAttributeKey(usernameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributesDto ofApple(String usernameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributesDto.builder()
                .nameAttributeKey(usernameAttributeName)
                .oAuth2UserInfo(new AppleOAuth2UserInfo(attributes))
                .build();
    }

    /**
     * of메소드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo가 소셜 타입별로 주입된 상태
     * OAuth2UserInfo에서 socialId(식별값), nickname, imageUrl을 가져와서 build
     * role은 GUEST로 설정
     */
    public User toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo) {
        return User.builder()
                .socialType(socialType)
                .socialId(oAuth2UserInfo.getId())
                .accountId(oAuth2UserInfo.getId())
//                .userImage(oAuth2UserInfo.getImageURL())
                .authorityRole(Role.GUEST)
                .build();
    }


}
