package com.seulmae.seulmae.global.config.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.util.PasswordUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.dto.response.LoginSuccessResponse;
import com.seulmae.seulmae.user.enums.Role;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * JWT 인증 필터
 * "/login" 이외의 모든 요청에 대해 처리하는 필터
 */

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    public static final List<String> NO_CHECK_URLS = Arrays.asList("/api/users/login", "/api/users/social-login");
    public static final String REFRESH_URL = "/api/token/refresh";
    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (NO_CHECK_URLS.contains(requestURI)) {
            filterChain.doFilter(request, response); // 로그인 url api 요청들어오면, 다음 필터 호출
            return;
        }

        /**
         * RefreshToken 타당성 검사 및 갱신
         */
        if (REFRESH_URL.equals(requestURI)) {
            // POST 메서드인지 확인
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                sendErrorResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method is required for token refresh", ErrorCode.METHOD_NOT_ALLOWED);
                return;
            }

            try {
                String refreshToken = jwtService.extractRefreshToken(request);
                jwtService.isValidToken(refreshToken);
                handleRefreshToken(response, refreshToken);
            } catch (TokenExpiredException e) {
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "RefreshToken has expired: " + e.getMessage(), ErrorCode.FORBIDDEN_ERROR);
                return;
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token Error: " + e.getMessage(), ErrorCode.BAD_REQUEST_ERROR);
            }

            return;
        }

        /**
         * 모든 경우에 해당하지 않는다면, accessToken이 유효한지 확인하고, 유효하면 인증처리 / 아니라면, 403 처리
         */

        // 리프레쉬 토큰이 없다거나 유효하지 않다면, accessToken을 검사하고 인증을 처리한다.
        // accessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        // 유효하다면, 인증 객체에 담긴 상태로 다음 필터로 넘어가기 때문에 인증성공
        checkAccessTokenAndAuthentication(request);
        filterChain.doFilter(request, response);
    }

    private void handleRefreshToken(HttpServletResponse response, String refreshToken) {

        checkRefreshToken(refreshToken).ifPresentOrElse(user -> {
            try {
                jwtService.sendAccessTokenAndRefreshToken(response, jwtService.createAccessToken(user.getAccountId()), refreshToken, user);
            } catch (IOException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), ErrorCode.BAD_REQUEST_ERROR);
            }
        }, () -> {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "해당 유저의 RefreshToken이 아닙니다.", ErrorCode.BAD_REQUEST_ERROR);
        });
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message, ErrorCode errorCode) {
        response.setStatus(status);
        response.setContentType(CONTENT_TYPE);

        try {
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(errorCode, message)));
        } catch (IOException e) {
            log.error("Error writing response", e);
            throw new RuntimeException("Error writing response", e);
        }
    }

    public Optional<User> checkRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    /**
     * 리프레시 토큰 재발급
     */
    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);

        return reIssuedRefreshToken;
    }

    /**
     * 엑세스 토큰을 요청에서 추출하고
     * 유효한지 확인한다음에,
     * 유효하면 아이디를 추출하고,
     * 해당 아이디를 통해 유저를 얻어내고,
     * 유저 객체를 authentication에 저장
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request) {
        jwtService.extractAccessToken(request)
                .filter(jwtService::isValidToken)
                .ifPresent(accessToken -> jwtService.extractAccountIdFromAccessToken(accessToken)
                        .ifPresent(accountId -> userRepository.findByAccountId(accountId)
                                .ifPresent(this::saveAuthentication)));
    }


    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     * <p>
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     * <p>
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */
    public void saveAuthentication(User user) {
        String password = user.getPassword();
        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = User.builder()
                .idUser(user.getIdUser())
                .accountId(user.getAccountId())
                .password(password)
                .authorityRole(Role.valueOf(user.getAuthorityRole().name()))
                .socialType(user.getSocialType())
                .socialId(user.getSocialId())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
