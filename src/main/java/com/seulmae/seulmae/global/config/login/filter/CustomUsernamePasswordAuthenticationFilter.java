package com.seulmae.seulmae.global.config.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
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


public class CustomUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/users/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";
    private static final String USERNAME_KEY = "accountId";
    private static final String PASSWORD_KEY = "password";

    private static final String FCM_TOKEN_KEY = "fcmToken";

    private final ObjectMapper objectMapper;

    public CustomUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD));
        this.objectMapper = objectMapper;
    }

    /**
     * 인증 처리 메소드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getContentType() == null || !request.getContentType().startsWith(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
        String accountId = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);
        String fcmToken = usernamePasswordMap.get(FCM_TOKEN_KEY);


        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(accountId, password);

        Map<String, Object> additionalDetails = new HashMap<>();
        additionalDetails.put(FCM_TOKEN_KEY, fcmToken);
        authRequest.setDetails(additionalDetails);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
