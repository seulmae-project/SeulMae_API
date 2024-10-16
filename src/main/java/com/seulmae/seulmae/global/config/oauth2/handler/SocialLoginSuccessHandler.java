package com.seulmae.seulmae.global.config.oauth2.handler;

import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.user.enums.Role;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.user.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SocialLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String accountId = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(accountId);
        String refreshToken = jwtService.createRefreshToken();

        User accountUser  = userRepository.findByAccountId(accountId)
                .orElse(null);

        if (accountUser != null) {
            // guest인 경우,
            if (accountUser.getAuthorityRole().equals(Role.GUEST)) {
                jwtService.sendAccessTokenAndRefreshToken(response, accessToken, null, accountUser);
            // guest가 아닌 경우,
            } else if (accountUser.getAuthorityRole().equals(Role.USER)) {
                accountUser.updateRefreshToken(refreshToken);

                String fcmToken = extractFcmToken(authentication);
                if (fcmToken != null && !fcmToken.isEmpty()) {
                    accountUser.addFcmToken(new FcmToken(extractFcmToken(authentication), accountUser));
                }

                userRepository.saveAndFlush(accountUser);

                jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken, accountUser);

                log.info("소셜 로그인에 성공하였습니다. 아이디: " + accountId);
                log.info("소셜 로그인에 성공하였습니다. AccessToken: " + accessToken);
                log.info("발급된 AccessToken 만료 기간: : " + accessTokenExpiration);
            }
        }
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private String extractFcmToken(Authentication authentication) {
        if (authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            return (String) details.get("fcmToken");
        }
        return null;
    }
}
