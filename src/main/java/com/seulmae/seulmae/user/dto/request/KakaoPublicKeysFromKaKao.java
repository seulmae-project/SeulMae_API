package com.seulmae.seulmae.user.dto.request;

import com.seulmae.seulmae.global.config.oauth2.kakao.KakaoOICDPublicKey;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KakaoPublicKeysFromKaKao implements PublicKeysFromOauth<KakaoOICDPublicKey> {

    private List<KakaoOICDPublicKey> keys;

    public KakaoPublicKeysFromKaKao(List<KakaoOICDPublicKey> keys) {
        this.keys = List.copyOf(keys);
    }

    @Override
    public KakaoOICDPublicKey getMatchingKey(String alg, String kid) {
        return keys.stream()
                .filter(key -> key.isSameAlg(alg) && key.isSameKid(kid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("잘못된 토큰 형태입니다."));
    }
}
