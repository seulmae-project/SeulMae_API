package com.seulmae.seulmae.global.config.oauth2.kakao;

import com.seulmae.seulmae.global.config.oauth2.OICDPublicKey;


public class KakaoOICDPublicKey extends OICDPublicKey {

    public KakaoOICDPublicKey(String kty, String kid, String alg, String use, String n, String e) {
        super(kty, kid, alg, use, n, e);
    }
}
