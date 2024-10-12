package com.seulmae.seulmae.global.config.oauth2;

import com.seulmae.seulmae.user.service.SocialLoginService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public class SocialAuthenticationProvider implements AuthenticationProvider {

    private final SocialLoginService socialLoginService;

    public SocialAuthenticationProvider(SocialLoginService socialLoginService) {
        this.socialLoginService = socialLoginService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (isSocialLogin((UsernamePasswordAuthenticationToken) authentication)) {
            String accountId = authentication.getName();
            UserDetails user = socialLoginService.loadUserByUsername(accountId);

            System.out.println("<<소셜로그인 인증>>");

            // 비밀번호 검증 없이 바로 인증 성공
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        }

        throw new AuthenticationServiceException("소셜 로그인 실패");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean isSocialLogin(UsernamePasswordAuthenticationToken authentication) {
        // 소셜 로그인 관련 추가 정보를 authentication 객체의 details에서 가져옴
        if (authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();

            // 예시: details에 "socialType" 필드가 존재하는 경우 소셜 로그인으로 간주
            String socialType = (String) details.get("provider");

            // socialType이 존재하고 값이 있다면 소셜 로그인으로 간주
            return socialType != null && !socialType.isEmpty();
        }

        return false;
    }
}
