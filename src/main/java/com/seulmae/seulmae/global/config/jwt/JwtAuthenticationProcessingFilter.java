package com.seulmae.seulmae.global.config.jwt;

import com.seulmae.seulmae.global.util.PasswordUtil;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
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
import java.util.Optional;


/**
 * JWT 인증 필터
 * "/login" 이외의 모든 요청에 대해 처리하는 필터
 */

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    public static final String NO_CHECK_URL = "/api/users/login";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response); // 로그인 url api 요청들어오면, 다음 필터 호출
            return;
        }

        // 리프레시 추출(없으면 null, 있다면 accessToken이 만료된 것)
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isValidToken)
                .orElse(null);
        
        if (refreshToken != null) {
            checkRefreshToken(refreshToken)
                    .ifPresent(user -> {
                        try {
                            jwtService.sendAccessTokenAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()), reIssueRefreshToken(user));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return;
        }

        // 리프레쉬 토큰이 없다거나 유효하지 않다면, accessToken을 검사하고 인증을 처리한다.
        // accessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        // 유효하다면, 인증 객체에 담긴 상태로 다음 필터로 넘어가기 때문에 인증성공
        checkAccessTokenAndAuthentication(request, response, filterChain);
        filterChain.doFilter(request, response);
    }


    public Optional<User> checkRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);

        return reIssuedRefreshToken;
    }

    /**
     * 엑세스 토큰을 요청에서 추출하고
     * 유효한지 확인한다음에,
     * 유효하면 이메일을 추출하고,
     * 해당 이메일을 통해 유저를 얻어내고,
     * 유저 객체를 authentication에 저장
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain) {
        jwtService.extractAccessToken(request)
                .filter(jwtService::isValidToken)
                .ifPresent(accessToken -> jwtService.extractEmailFromAccessToken(accessToken)
                        .ifPresent(email -> userRepository.findByEmail(email)
                                .ifPresent(this::saveAuthentication)));
    }


    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     *
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     *
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
                .email(user.getEmail())
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
