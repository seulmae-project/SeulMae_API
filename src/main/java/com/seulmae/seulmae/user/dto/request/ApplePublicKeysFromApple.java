package com.seulmae.seulmae.user.dto.request;

import com.seulmae.seulmae.global.config.oauth2.apple.AppleOICDPublicKey;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApplePublicKeysFromApple implements PublicKeysFromOauth<AppleOICDPublicKey>  {

    private List<AppleOICDPublicKey> keys;

    public ApplePublicKeysFromApple(List<AppleOICDPublicKey> keys) {
        this.keys = List.copyOf(keys);
    }
    @Override
    public AppleOICDPublicKey getMatchingKey(final String alg, final String kid) {
        return keys.stream()
                .filter(key -> key.isSameAlg(alg) && key.isSameKid(kid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("잘못된 토큰 형태입니다."));
    }
}