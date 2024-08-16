package com.seulmae.seulmae.global.config.oauth2;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class SocialAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accountId = authentication.getName();
        // 이 부분에서 소셜 로그인 시 필요한 추가 검증 로직을 수행
        // 예를 들어, 계정의 유효성을 확인하거나, 추가 사용자 정보를 조회

        // 비밀번호 없이 사용자 인증 처리
        return new UsernamePasswordAuthenticationToken(accountId, null, authentication.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
