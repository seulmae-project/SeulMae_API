package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.OAuthAttributesDto;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final SocialLoginService socialLoginService;

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;


    /** 유저 info를 부르고 user를 반환하는 메서드 **/
    public User getUserInfo(String token, String provider) {
        String url = UriComponentsBuilder.fromHttpUrl(USER_INFO_URI)
                .pathSegment("v2", "user", "me")
                .toUriString();

        // HTTP GET 요청을 보내고, KakaoUserInfo 객체로 응답을 변환합니다.
        Map<String, Object> attributes = restTemplate.getForObject(url, Map.class, token);
        SocialType socialType = socialLoginService.getSocialType(provider);

        String socialId = attributes.get("id").toString();
        OAuthAttributesDto extractAttributes = OAuthAttributesDto.of(socialType, socialId, attributes);

        return socialLoginService.getOrCreateUser(extractAttributes, socialType);
    }

}
