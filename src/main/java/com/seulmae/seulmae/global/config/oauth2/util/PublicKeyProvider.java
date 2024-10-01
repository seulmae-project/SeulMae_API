package com.seulmae.seulmae.global.config.oauth2.util;

import com.seulmae.seulmae.global.config.oauth2.OICDPublicKey;
import com.seulmae.seulmae.global.config.oauth2.kakao.KakaoOICDPublicKey;
import com.seulmae.seulmae.user.dto.request.PublicKeysFromOauth;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class PublicKeyProvider {
    private static final String SIGN_ALGORITHM_HEADER = "alg";
    private static final String KEY_ID_HEADER = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;

    public static PublicKey generate(Map<String, String> header, PublicKeysFromOauth<OICDPublicKey> publicKeys) {
        final OICDPublicKey oicdPublicKey = publicKeys.getMatchingKey(
                header.get(SIGN_ALGORITHM_HEADER),
                header.get(KEY_ID_HEADER)
        );
        return generatePublicKey(oicdPublicKey);
    }

    private static PublicKey generatePublicKey(OICDPublicKey oicdPublicKey) {
        final byte[] nBytes = Base64.getUrlDecoder().decode(oicdPublicKey.getN());
        final byte[] eBytes = Base64.getUrlDecoder().decode(oicdPublicKey.getE());

        final BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        final BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);
        final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(oicdPublicKey.getKty());
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new RuntimeException("잘못된 퍼블릭 키입니다.");
        }
    }
}
