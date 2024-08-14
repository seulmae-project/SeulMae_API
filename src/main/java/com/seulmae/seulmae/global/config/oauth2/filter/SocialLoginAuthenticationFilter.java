package com.seulmae.seulmae.global.config.oauth2.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.service.AppleService;
import com.seulmae.seulmae.user.service.KakaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class SocialLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 인증 시도
     * 0. 공통
     * - token과 provider가 들어온다.
     * 1. 카카오
     * - accessToken으로 유저 정보를 조회한다.
     * - 조회해서 나온 id가 기존 db에 있는지 확인 후, 들어와있다면 로그인 적용
     * - 새로운 유저라면, 회원가입 & jwt 발급 이후, 로그인
     * 2. 애플
     * - identityToken으로 public key를 조회하고, public key를 이용하여 유저 정보를 조회한다.
     * - 이하는 카카오와 동일하다.
     */

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/users/login/social";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";
    private static final String TOKEN_KEY = "token";
    private static final String PROVIDER_KEY = "provider";
    private static final String FCM_TOKEN_KEY = "fcmToken";
    private static final String KAKAO = "kakao";
    private static final String APPLE = "apple";


    private final ObjectMapper objectMapper;
    private final KakaoService kakaoService;
    private final AppleService appleService;

    public SocialLoginAuthenticationFilter(ObjectMapper objectMapper, KakaoService kakaoService, AppleService appleService) {
        super(new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD));
        this.objectMapper = objectMapper;
        this.kakaoService = kakaoService;
        this.appleService = appleService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getContentType() == null || !request.getContentType().startsWith(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> keyMap = objectMapper.readValue(messageBody, Map.class);
        String token = keyMap.get(TOKEN_KEY);
        String provider = keyMap.get(PROVIDER_KEY);
        String fcmToken = keyMap.get(FCM_TOKEN_KEY);

        /**
         * social Login 분기점
         */
        String accountId;
        if (provider.equals(KAKAO)) {
            accountId = kakaoService.getUserInfo(token).getAccountId();
        } else if (provider.equals(APPLE)) {
            accountId = appleService.getUserInfo(token).getAccountId();
        } else {
            throw new NoSuchElementException("해당 소셜 로그인 종류는 존재하지 않습니다.");
        }


        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(accountId, null);

        Map<String, Object> additionalDetails = new HashMap<>();
        additionalDetails.put(FCM_TOKEN_KEY, fcmToken);
        authRequest.setDetails(additionalDetails);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
