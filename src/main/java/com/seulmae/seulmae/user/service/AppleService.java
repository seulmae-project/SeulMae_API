package com.seulmae.seulmae.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.config.oauth2.apple.ApplePublicKey;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.controller.AppleClient;
import com.seulmae.seulmae.user.dto.request.ApplePublicKeysFromApple;
import com.seulmae.seulmae.user.dto.request.OAuthAttributesDto;
import com.seulmae.seulmae.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
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
    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;
    private static final String SIGN_ALGORITHM_HEADER = "alg";
    private static final String KEY_ID_HEADER = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;
    private static final String CLAIM_ID = "sub";


    private final ObjectMapper objectMapper;
    private final AppleClient appleClient;
    private final SocialLoginService socialLoginService;

    public User getUserInfo(String identityToken, String provider) {
        final Map<String, String> appleTokenHeader = parseHeader(identityToken);
        final ApplePublicKeysFromApple applePublicKeys = appleClient.getApplePublicKeys();
        final PublicKey publicKey = generate(appleTokenHeader, applePublicKeys);
        final Map<String, Object> claims = new HashMap<>(extractClaims(identityToken, publicKey));

        SocialType socialType = socialLoginService.getSocialType(provider);

        OAuthAttributesDto extractAttributes = OAuthAttributesDto.of(socialType, claims.get(CLAIM_ID).toString(), claims);

        return socialLoginService.getOrCreateUser(extractAttributes, socialType);
    }

    /**
     * id_token 헤더 추출
     */

    public Map<String, String> parseHeader(final String appleToken) {
        try {
            final String encodedHeader = appleToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            final String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return objectMapper.readValue(decodedHeader, Map.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException("appleToken 값이 jwt 형식인지, 값이 정상적인지 확인해주세요.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("디코드된 헤더를 Map 형태로 분류할 수 없습니다. 헤더를 확인해주세요.");
        }
    }

    private Claims extractClaims(final String appleToken, final PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(appleToken)
                    .getPayload();
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 jwt 타입");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("비어있는 jwt");
        } catch (JwtException e) {
            throw new JwtException("jwt 검증 or 분석 오류");
        }
    }

    private PublicKey generate(final Map<String, String> headers, final ApplePublicKeysFromApple publicKeys) {
        final ApplePublicKey applePublicKey = publicKeys.getMatchingKey(
                headers.get(SIGN_ALGORITHM_HEADER),
                headers.get(KEY_ID_HEADER)
        );
        return generatePublicKey(applePublicKey);
    }

    private PublicKey generatePublicKey(final ApplePublicKey applePublicKey) {
        final byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKey.getN());
        final byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKey.getE());

        final BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        final BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);
        final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.getKty());
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new RuntimeException("잘못된 애플 키");
        }
    }
}
