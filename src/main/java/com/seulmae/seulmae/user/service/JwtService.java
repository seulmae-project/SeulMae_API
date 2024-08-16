package com.seulmae.seulmae.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.dto.response.LoginSuccessResponse;
import com.seulmae.seulmae.user.dto.response.TokenResponse;
import com.seulmae.seulmae.user.dto.response.WorkplaceResponse;
import com.seulmae.seulmae.user.entity.CustomOAuth2User;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j //로깅 기능
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String ACCOUNT_CLAIM = "accountId";
    private static final String BEARER = "Bearer ";
    private static final String CONTENT_TYPE = "application/json";

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * AccessToken 생성
     */
    public String createAccessToken(String accountId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(ACCOUNT_CLAIM, accountId) // 클레임으로 accountId(식별자) 사용
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * AccessToken 헤더에 송출하기
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(CONTENT_TYPE);
        response.getWriter()
                .write(objectMapper.writeValueAsString(
                                new TokenResponse(accessToken, BEARER)
                        )
                );
//        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token: ", accessToken);
    }

    /**
     * AccessToken & RefreshToken 바디(헤더는 임시 주석처리)에 송출
     */
    public void sendAccessTokenAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken, List<UserWorkplace> userWorkplaces) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(CONTENT_TYPE);
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken, BEARER);
        List<WorkplaceResponse> workplaceResponses = userWorkplaces.stream()
                        .map(userWorkplace -> new WorkplaceResponse(userWorkplace)).collect(Collectors.toList());
        response.getWriter()
                .write(objectMapper.writeValueAsString(
                        new SuccessResponse(SuccessCode.LOGIN_SUCCESS, new LoginSuccessResponse(tokenResponse, null, workplaceResponses))
                        )
                );
//        response.setHeader(accessHeader, accessToken);
//        response.setHeader(refreshHeader, refreshToken);
        log.info("AccessToken & RefreshToken 바디 전달 완료");

    }

    /**
     * 소셜로그인용 첫 로그인시 사용하는 메서드(GUEST 추가 정보 얻기 위함)
     */
    public void sendAccessTokenAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken, User user) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(CONTENT_TYPE);
        Role role = user.getAuthorityRole();
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken, BEARER);
        response.getWriter()
                .write(objectMapper.writeValueAsString(
                                new SuccessResponse(SuccessCode.LOGIN_SUCCESS, new LoginSuccessResponse(tokenResponse, role, null))
                        )
                );
//        response.setHeader(accessHeader, accessToken);
//        response.setHeader(refreshHeader, refreshToken);
        log.info("AccessToken & RefreshToken 바디 전달 완료");

    }

    /**
     * AccessToken 추출
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }


    /**
     * RefreshToken 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * AccessToken에서 아이디 추출
     */
    public Optional<String> extractAccountIdFromAccessToken(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build() // 반환된 빌더로 JWT verifier 생성
                    .verify(accessToken) // 검증하고 유효하지 않다면 예외 발생
                    .getClaim(ACCOUNT_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(String accountId, String refreshToken) {
        userRepository.findByAccountId(accountId)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new NoSuchElementException("일치하는 회원이 존재하지 않습니다.")
                );
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean isValidToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다 [" + e.getMessage() + "]");
            return false;
        }
    }


    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    /**
     * RefreshToken 헤더 설정
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }
}
