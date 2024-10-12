package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.user.dto.request.KakaoPublicKeysFromKaKao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "kakao-public-key",
        url = "https://kauth.kakao.com")
public interface KakaoClient {
    @Cacheable(cacheNames = "KakaoPublicKeys", cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    KakaoPublicKeysFromKaKao getKakaoOICDOpenKeys();
}
