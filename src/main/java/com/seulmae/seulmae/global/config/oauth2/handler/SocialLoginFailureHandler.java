package com.seulmae.seulmae.global.config.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class SocialLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter()
                .write(
                        objectMapper.writeValueAsString(
                                ResponseUtil.createErrorResponse(ErrorCode.UNAUTHORIZED, exception.getMessage()).getBody()
                        )
                );
        log.info("소셜 로그인 실패: " + exception.getMessage());
    }
}
