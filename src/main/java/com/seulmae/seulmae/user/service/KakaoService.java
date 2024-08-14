package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.config.oauth2.userInfo.OAuth2UserInfo;
import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.KaKaoUserInfo;
import com.seulmae.seulmae.user.dto.request.OAuthAttributesDto;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final JwtService jwtService;

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    @Value("${file.storage.path.user}")
    private String userFilePath;


    /** 유저 info를 부르고 user를 반환하는 메서드 **/
    public User getUserInfo(String token) {
        String url = UriComponentsBuilder.fromHttpUrl(USER_INFO_URI)
                .pathSegment("v2", "user", "me")
                .toUriString();

        // HTTP GET 요청을 보내고, KakaoUserInfo 객체로 응답을 변환합니다.
        KaKaoUserInfo kaKaoUserInfo = restTemplate.getForObject(url, KaKaoUserInfo.class, token);
        return getOrCreateUser(kaKaoUserInfo);
    }


    /** 해당 정보로 유저를 조회하는 메서드 **/
    public User getOrCreateUser(KaKaoUserInfo kaKaoUserInfo) {
        User findUser = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, kaKaoUserInfo.getSocialId()).orElse(null);

        if (findUser == null) {
            User savedUser = saveUser(kaKaoUserInfo);
            saveUserImage(savedUser, kaKaoUserInfo);
            return savedUser;
        }

        return findUser;
    }


//    private SocialType getSocialType(String registrationId) {
//        if (KAKAO.equals(registrationId)) {
//            return SocialType.KAKAO;
//        }
//
//        return null;
//    }

    /** 회원 가입하는 메서드 **/
    private User saveUser(KaKaoUserInfo kaKaoUserInfo) {
        User user = kaKaoUserInfo.toEntity();
        return userRepository.save(user);
    }

    private void saveUserImage(User user, KaKaoUserInfo kaKaoUserInfo) {
        String imageURL = kaKaoUserInfo.getKakaoAccount().getProfile().getThumbnailImageUrl();
        String imageUrlName = FileUtil.extractUrlName(imageURL);
        String imageUrlPath = userFilePath + user.getIdUser();

        UserImage userImage = new UserImage(user, imageUrlName, imageUrlPath, FileUtil.getFileExtension(imageURL));
        user.updateUserImage(userImage);
        FileUtil.downloadImageFromUrl(imageUrlPath, imageUrlName, imageURL);
        userRepository.save(user);
    }
}
