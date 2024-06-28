package com.seulmae.seulmae.global.config.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.entity.CustomOAuth2User;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 로그인 성공!");
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 첫 로그인인 경우
        if (oAuth2User.getRole().equals(Role.GUEST)) {
            String accessToken = jwtService.createAccessToken(oAuth2User.getAccountId());
//            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//            response.sendRedirect("api/users/signup"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
            jwtService.sendAccessTokenAndRefreshToken(response, accessToken, null, oAuth2User);

        } else {
            loginSuccess(response, oAuth2User);
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무/만기에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getAccountId());
        String refreshToken = jwtService.createRefreshToken();
//        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getAccountId(), refreshToken);
    }
}
