package com.seulmae.seulmae.global.config.oauth2.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.config.oauth2.OICDPublicKey;
import com.seulmae.seulmae.user.controller.KakaoClient;
import com.seulmae.seulmae.user.dto.request.PublicKeysFromOauth;
import com.seulmae.seulmae.user.service.SocialLoginService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdTokenValidator {
    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;
    private static final int PAYLOAD_INDEX = 1;
    private static final String KEY_ID_HEADER = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;



    private final ObjectMapper objectMapper;

    /**
     * 페이로드 검증
     * 1. id 토큰의 영역 구분자인 온점(.)을 기준으로 페이로드 뽑아내기(인덱스 1)
     * 2. 페이로드를 Base64 방식으로 DECODING
     * 3. 페이로드의 키별 값 검증
     * 3-1. iss: https://kauth.kakao.com 또는 https://appleid.apple.com와 일치해야 함
     * 3-2. aud: 서비스 앱 키(카카오) 또는 클라이언트 아이디(애플)와 일치해야 함
     * 3-3. exp: 현재 UNIX 타임스탬프보다 큰 값 필요(ID 토큰 만료 여부 확인)
     * 3-4. nonce: 카카오 로그인 요청시 전달한 값과 일치해야 함
     */
    private Map<String, String> parsePayload(final String idToken) {
        try {
            final String encodedPayload = idToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[PAYLOAD_INDEX];
            final String decodedPayload = new String(Base64.getUrlDecoder().decode(encodedPayload));
            return objectMapper.readValue(decodedPayload, Map.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException("idToken 값이 jwt 형식인지, 값이 정상적인지 확인해주세요.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("디코딩된 페이로드를 Map 형태로 분류할 수 없습니다. 페이로드를 확인해주세요.");
        }
    }

    public Jwt<Header, Claims> validatePayloadClaims(String idToken, String iss, String aud) {
        try {
            return Jwts.parser()
                    .requireAudience(aud)
                    .requireIssuer(iss)
                    .build()
                    .parseUnsecuredClaims(parsePayload(idToken).get("decodedPayload"));
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("ID 토큰이 만료됐습니다");
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException("유효하지 않는 ID 토큰입니다");
        }
    }

    /**
     * 서명 검증
     * 1. id 토큰의 영역 구분자인 온점(.)을 기준으로 헤더, 서명 뽑아내기(인덱스 0, 2)
     * 2. 헤더를 Base64 방식으로 디코딩
     * 3. OIDC: 공개키 목록 조회하기 API로 카카오 인증 서버가 서명 시 사용하는 공개키 목록 조회
     * 4. 공개키 목록에서 헤더의 kid에 해당하는 공개키 값 확인
     * 5. JWT 서명 검증을 지원하는 라이브러리를 사용해 공개키로 서명 검증
     */

    private Map<String, String> parseHeader(final String idToken) {
        try {
            final String encodedHeader = idToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            final String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return objectMapper.readValue(decodedHeader, Map.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException("idToken 값이 jwt 형식인지, 값이 정상적인지 확인해주세요.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("디코드된 헤더를 Map 형태로 분류할 수 없습니다. 헤더를 확인해주세요.");
        }
    }

    // 공개키로 토큰 검증을 시도하고 클레임을 추출한다.
    public Claims extractClaims(String idToken, PublicKeysFromOauth<OICDPublicKey> publicKeys) {
        try {
            return Jwts.parser()
                    .verifyWith(PublicKeyProvider.generate(parseHeader(idToken), publicKeys))
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 jwt 타입");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("비어있는 jwt");
        } catch (JwtException e) {
            throw new JwtException("jwt 검증 or 분석 오류");
        }
    }
}
