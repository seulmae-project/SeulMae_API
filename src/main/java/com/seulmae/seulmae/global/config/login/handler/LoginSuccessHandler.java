package com.seulmae.seulmae.global.config.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
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
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

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
                .map(user -> {
                    user.updateRefreshToken(refreshToken);
                    user.addFcmToken(new FcmToken(extractFcmToken(authentication), user));

                    return userRepository.saveAndFlush(user);
                })
                .orElse(null);

//        List<UserWorkplace> userWorkplaces = userWorkplaceRepository.findAllByUser(accountUser);
        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken, accountUser);

        log.info("로그인에 성공하였습니다. 아이디: " + accountId);
        log.info("로그인에 성공하였습니다. AccessToken: " + accessToken);
        log.info("발급된 AccessToken 만료 기간: : " + accessTokenExpiration);

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
