package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.user.dto.request.ApplePublicKeysFromApple;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-public-key", url = "https://appleid.apple.com")
public interface AppleClient {
    @GetMapping("/auth/keys")
    ApplePublicKeysFromApple getApplePublicKeys();
}
