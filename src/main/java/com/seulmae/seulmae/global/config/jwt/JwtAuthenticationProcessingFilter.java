package com.seulmae.seulmae.global.config.jwt;

import com.seulmae.seulmae.global.util.PasswordUtil;
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

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (NO_CHECK_URLS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response); // 로그인 url api 요청들어오면, 다음 필터 호출
            return;
        }

        // 리프레시 추출(없으면 null, 있다면 accessToken이 만료된 것)
        /**
         * 추출해서, 토큰이 타당한지 확인하고, 없으면 null.
         */
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isValidToken)
                .orElse(null);


        /**
         * 만약 리프레시 토큰이 있다면, 유저db에 존재하는 토큰인지 확인하고, 존재한다면, 리프레시 토큰 재발급 후 로그인
         *
         */
        if (refreshToken != null) {
            checkRefreshToken(refreshToken)
                    .ifPresent(user -> {
                        try {
//                            List<UserWorkplace> userWorkplaces = userWorkplaceRepository.findAllByUser(user);
                            jwtService.sendAccessTokenAndRefreshToken(response, jwtService.createAccessToken(user.getAccountId()), reIssueRefreshToken(user), user);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return;
        }

        /**
         * 없다면, accessToken이 유효한지 확인하고, 유효하면 인증처리 / 아니라면, 403 처리
         */

        // 리프레쉬 토큰이 없다거나 유효하지 않다면, accessToken을 검사하고 인증을 처리한다.
        // accessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        // 유효하다면, 인증 객체에 담긴 상태로 다음 필터로 넘어가기 때문에 인증성공
        checkAccessTokenAndAuthentication(request, response, filterChain);
        filterChain.doFilter(request, response);
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
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain) {
        jwtService.extractAccessToken(request)
                .filter(jwtService::isValidToken)
                .ifPresent(accessToken -> jwtService.extractAccountIdFromAccessToken(accessToken)
                        .ifPresent(accountId -> userRepository.findByAccountId(accountId)
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
