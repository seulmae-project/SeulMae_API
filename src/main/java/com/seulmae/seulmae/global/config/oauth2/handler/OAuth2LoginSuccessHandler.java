package com.seulmae.seulmae.global.config.oauth2.handler;

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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 로그인 성공!");
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 첫 로그인인 경우, 회원가입으로 리다이렉트
        if (oAuth2User.getRole().equals(Role.GUEST)) {
            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            response.sendRedirect("api/users/signup"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
            // 이 부분 수정해야겠다.

            jwtService.sendAccessTokenAndRefreshToken(response, accessToken, null);
            /**
             * 주석 처리한 부분 - Role을 GUEST -> USER로 업데이트하는 로직입니다.
             * 지금은 회원가입 추가 폼 입력 시 업데이트하는 컨트롤러를 만들지 않아서 저렇게 놔뒀습니다.
             * 이후에 회원가입 추가 폼 입력 시 업데이트하는 컨트롤러, 서비스를 만들면
             * 그 시점에 Role Update를 진행하면 될 것 같습니다.
             */
//                User findUser = userRepository.findByEmail(oAuth2User.getEmail())
//                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
//                findUser.authorizeUser();
        } else {
            loginSuccess(response, oAuth2User);
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무/만기에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
