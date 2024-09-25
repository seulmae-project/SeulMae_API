package com.seulmae.seulmae.global.config.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ResponseUtil.createErrorResponse(ErrorCode.UNAUTHORIZED, exception.getMessage()).getBody()
                )
        );
        response.getWriter().flush();
        response.getWriter().close();

        log.info("로그인 실패: " + exception.getMessage());
    }

}
