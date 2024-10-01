package com.seulmae.seulmae.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.config.oauth2.apple.AppleOICDPublicKey;
import com.seulmae.seulmae.global.config.oauth2.util.IdTokenValidator;
import com.seulmae.seulmae.user.dto.request.PublicKeysFromOauth;
import com.seulmae.seulmae.user.enums.SocialType;
import com.seulmae.seulmae.user.controller.AppleClient;
import com.seulmae.seulmae.user.dto.request.ApplePublicKeysFromApple;
import com.seulmae.seulmae.user.dto.request.OAuthAttributesDto;
import com.seulmae.seulmae.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppleService {
    @Value("${social-login.provider.apple.client-id}")
    private String APPLE_AUD;
    private static final String APPLE_ISS = "https://appleid.apple.com";
    private static final String CLAIM_ID = "sub";


    private final AppleClient appleClient;
    private final SocialLoginService socialLoginService;
    private final IdTokenValidator idTokenValidator;
    public User getUserInfo(String idToken, String provider) {
        PublicKeysFromOauth applePublicKeys = appleClient.getApplePublicKeys();
        // 임시 주석 처리(클라이언트 id를 받을 수 없는 상태)
//        idTokenValidator.validatePayloadClaims(idToken, APPLE_ISS, APPLE_AUD);
        Map<String, Object> claims = new HashMap<>(idTokenValidator.extractClaims(idToken, applePublicKeys));

        SocialType socialType = socialLoginService.getSocialType(provider);
        OAuthAttributesDto extractAttributes = OAuthAttributesDto.of(socialType, claims.get(CLAIM_ID).toString(), claims);

        return socialLoginService.getOrCreateUser(extractAttributes, socialType);
    }
//    public User getUserInfo(String identityToken, String provider) {
//        final Map<String, String> appleTokenHeader = parseHeader(identityToken);
//        final ApplePublicKeysFromApple applePublicKeys = appleClient.getApplePublicKeys();
//        final PublicKey publicKey = generate(appleTokenHeader, applePublicKeys);
//        final Map<String, Object> claims = new HashMap<>(extractClaims(identityToken, publicKey));
//
//        SocialType socialType = socialLoginService.getSocialType(provider);
//
//        OAuthAttributesDto extractAttributes = OAuthAttributesDto.of(socialType, claims.get(CLAIM_ID).toString(), claims);
//
//        return socialLoginService.getOrCreateUser(extractAttributes, socialType);
//    }
//
//    /**
//     * id_token 헤더 추출
//     */
//
//    public Map<String, String> parseHeader(final String appleToken) {
//        try {
//            final String encodedHeader = appleToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
//            final String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
//            return objectMapper.readValue(decodedHeader, Map.class);
//        } catch (JsonMappingException e) {
//            throw new RuntimeException("idToken 값이 jwt 형식인지, 값이 정상적인지 확인해주세요.");
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("디코드된 헤더를 Map 형태로 분류할 수 없습니다. 헤더를 확인해주세요.");
//        }
//    }
//
//    private Claims extractClaims(final String appleToken, final PublicKey publicKey) {
//        try {
//            return Jwts.parser()
//                    .verifyWith(publicKey)
//                    .build()
//                    .parseSignedClaims(appleToken)
//                    .getPayload();
//        } catch (UnsupportedJwtException e) {
//            throw new UnsupportedJwtException("지원되지 않는 jwt 타입");
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("비어있는 jwt");
//        } catch (JwtException e) {
//            throw new JwtException("jwt 검증 or 분석 오류");
//        }
//    }
//
//    private PublicKey generate(final Map<String, String> headers, final ApplePublicKeysFromApple publicKeys) {
//        final AppleOICDPublicKey applePublicKey = publicKeys.getMatchingKey(
//                headers.get(SIGN_ALGORITHM_HEADER),
//                headers.get(KEY_ID_HEADER)
//        );
//        return generatePublicKey(applePublicKey);
//    }
//
//    private PublicKey generatePublicKey(final AppleOICDPublicKey applePublicKey) {
//        final byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKey.getN());
//        final byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKey.getE());
//
//        final BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
//        final BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);
//        final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
//
//        try {
//            final KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.getKty());
//            return keyFactory.generatePublic(rsaPublicKeySpec);
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
//            throw new RuntimeException("잘못된 애플 키");
//        }
//    }
}
