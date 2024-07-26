package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.config.oauth2.userInfo.OAuth2UserInfo;
import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.OAuthAttributesDto;
import com.seulmae.seulmae.user.entity.CustomOAuth2User;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    private final UserImageService userImageService;

    @Value("${file.storage.path.user}")
    private String userFilePath;

    private static final String KAKAO = "kakao";


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 로그인 요청 진입");
        // DefaultOAuth2UserService는 loadUser를 통해 DefaultOAuth2User를 반환한다.
        // loadUser는 사용자 정보 제공 uri로 요청을 보내서, userInfo를 얻어낸다.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributionName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 소셜타입에 따른 OAuthAttributesDto 객체 생성
        OAuthAttributesDto extractAttributes = OAuthAttributesDto.of(socialType, userNameAttributionName, attributes);

        User user = getUser(extractAttributes, socialType);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthorityRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                user.getAccountId(),
                user.getAuthorityRole()
        );

    }

    private SocialType getSocialType(String registrationId) {
        if (KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }

        return null;
    }

    private User getUser(OAuthAttributesDto attributes, SocialType socialType) {
        User findUser = userRepository.findBySocialTypeAndSocialId(socialType, attributes.getOAuth2UserInfo().getId()).orElse(null);

        if (findUser == null) {
            User savedUser = saveUser(attributes, socialType);
            saveUserImage(savedUser, attributes.getOAuth2UserInfo());
            return savedUser;
        }

        return findUser;
    }

    private User saveUser(OAuthAttributesDto attributes, SocialType socialType) {
        User user = attributes.toEntity(socialType, attributes.getOAuth2UserInfo());
        return userRepository.save(user);
    }

    private void saveUserImage(User user, OAuth2UserInfo oAuth2UserInfo) {
        String imageURL = oAuth2UserInfo.getImageURL();
        String imageUrlName = FileUtil.extractUrlName(imageURL);
        String imageUrlPath = userFilePath + user.getIdUser();

        UserImage userImage = new UserImage(user, imageUrlName, imageUrlPath, FileUtil.getFileExtension(imageURL));
        user.updateUserImage(userImage);
        FileUtil.downloadImageFromUrl(imageUrlPath, imageUrlName, imageURL);
        userRepository.save(user);
    }
}
