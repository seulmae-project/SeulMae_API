package com.seulmae.seulmae.global.config.oauth2;

import com.seulmae.seulmae.user.service.SocialLoginService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class SocialAuthenticationProvider implements AuthenticationProvider {

    private final SocialLoginService socialLoginService;

    public SocialAuthenticationProvider(SocialLoginService socialLoginService) {
        this.socialLoginService = socialLoginService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accountId = authentication.getName();
        UserDetails user = socialLoginService.loadUserByUsername(accountId);

        // 비밀번호 검증 없이 바로 인증 성공
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
