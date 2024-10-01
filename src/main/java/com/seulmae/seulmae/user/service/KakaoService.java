package com.seulmae.seulmae.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.config.oauth2.OICDPublicKey;
import com.seulmae.seulmae.global.config.oauth2.kakao.KakaoOICDPublicKey;
import com.seulmae.seulmae.global.config.oauth2.util.IdTokenValidator;
import com.seulmae.seulmae.user.controller.KakaoClient;
import com.seulmae.seulmae.user.dto.request.KakaoPublicKeysFromKaKao;
import com.seulmae.seulmae.user.dto.request.OAuthAttributesDto;
import com.seulmae.seulmae.user.dto.request.PublicKeysFromOauth;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.enums.SocialType;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.apache.http.message.BasicLineParser.parseHeader;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    @Value("${social-login.provider.kakao.native-app-key}")
    private String KAKAO_AUD;
    private static final String KAKAO_ISS = "https://kauth.kakao.com";
    private static final String CLAIM_ID = "sub";

    private final SocialLoginService socialLoginService;
    private final KakaoClient kakaoClient;
    private final IdTokenValidator idTokenValidator;


    @Value("${social-login.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    /**
     * 카카오 소셜 로그인 OICD 이후, 회원가입 또는 회원조회 진행
     * - ID토큰 유효성 검사
     * - 페이로드 검증
     * - 서명 검증
     *   - 공개키 찾기
     *   - 공개키 생성
     *   - 공개키를 이용해서 서명 검증
     * - 유저 정보 조회
     */
    public User getUserInfo(String idToken, String provider) {
        PublicKeysFromOauth kakaoPublicKeys = kakaoClient.getKakaoOICDOpenKeys();
        idTokenValidator.validatePayloadClaims(idToken, KAKAO_ISS, KAKAO_AUD);
        Map<String, Object> claims = new HashMap<>(idTokenValidator.extractClaims(idToken, kakaoPublicKeys));
        SocialType socialType = socialLoginService.getSocialType(provider);
        OAuthAttributesDto extractAttributes = OAuthAttributesDto.of(socialType, claims.get(CLAIM_ID).toString(), claims);

        return socialLoginService.getOrCreateUser(extractAttributes, socialType);
    }


    /** 유저 info를 부르고 user를 반환하는 메서드 **/
//    public User getUserInfo(String token, String provider) {
//        String url = UriComponentsBuilder.fromHttpUrl(USER_INFO_URI)
//                .pathSegment("v2", "user", "me")
//                .toUriString();
//
//        // HTTP GET 요청을 보내고, KakaoUserInfo 객체로 응답을 변환합니다.
//        Map<String, Object> attributes = restTemplate.getForObject(url, Map.class, token);
//        SocialType socialType = socialLoginService.getSocialType(provider);
//
//        String socialId = attributes.get("id").toString();
//        OAuthAttributesDto extractAttributes = OAuthAttributesDto.of(socialType, socialId, attributes);
//
//        return socialLoginService.getOrCreateUser(extractAttributes, socialType);
//    }

}
