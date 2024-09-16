package com.seulmae.seulmae.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.config.jwt.JwtAuthenticationProcessingFilter;
import com.seulmae.seulmae.global.config.login.filter.CustomUsernamePasswordAuthenticationFilter;
import com.seulmae.seulmae.global.config.login.handler.LoginFailureHandler;
import com.seulmae.seulmae.global.config.login.handler.LoginSuccessHandler;
import com.seulmae.seulmae.global.config.oauth2.SocialAuthenticationProvider;
import com.seulmae.seulmae.global.config.oauth2.filter.SocialLoginAuthenticationFilter;
//import com.seulmae.seulmae.global.config.oauth2.handler.OAuth2LoginFailureHandler;
//import com.seulmae.seulmae.global.config.oauth2.handler.OAuth2LoginSuccessHandler;
import com.seulmae.seulmae.global.config.oauth2.handler.SocialLoginFailureHandler;
import com.seulmae.seulmae.global.config.oauth2.handler.SocialLoginSuccessHandler;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginService loginService;
    private final SocialLoginService socialLoginService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;
    private final ObjectMapper objectMapper;
//    private final CustomOAuth2UserService customOAuth2UserService;
    private final KakaoService kakaoService;
    private final AppleService appleService;

    @Bean
    public WebSecurityCustomizer configure() { //스프링 시큐리티 기능 비활성화하는 곳
        return (web) -> web.ignoring()
                .requestMatchers("/h2-console/**")
                .requestMatchers(
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable)

                // 세션 사용하지 않으므로 STATELESS 설정
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/file").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/workplace/file").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/sms-certification/send").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/sms-certification/confirm").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/id/duplication").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/users/email/search").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/pw").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                        .anyRequest().permitAll()) //여기 부분 다시 고민해보자
//                .oauth2Login(oauth2 -> oauth2
//                        .successHandler(oAuth2LoginSuccessHandler)
//                        .failureHandler(oAuth2LoginFailureHandler)
//                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService))
//                )

                // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
                // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
                // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
                .addFilterAfter(jwtAuthenticationProcessingFilter(), LogoutFilter.class)
                .addFilterAfter(socialLoginAuthenticationFilter(), JwtAuthenticationProcessingFilter.class)
                .addFilterAfter(customUsernamePasswordAuthenticationFilter(), SocialLoginAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정 후 등록
     * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     * UserDetailsService는 커스텀 LoginService로 등록
     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
     *
     */
    @Bean
    public AuthenticationManager authenticationManager() {

        SocialAuthenticationProvider socialLoginProvider = new SocialAuthenticationProvider(socialLoginService);

        DaoAuthenticationProvider loginProvider = new DaoAuthenticationProvider();
        loginProvider.setPasswordEncoder(passwordEncoder());
        loginProvider.setUserDetailsService(loginService);

        return new ProviderManager(socialLoginProvider, loginProvider);
    }

    /**
     * 핸들러 빈 등록
     */
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, userRepository, userWorkplaceRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler(objectMapper);
    }

    @Bean
    public SocialLoginSuccessHandler socialLoginSuccessHandler() {
        return new SocialLoginSuccessHandler(jwtService, userRepository, userWorkplaceRepository);
    }

    @Bean
    public SocialLoginFailureHandler socialLoginFailureHandler() {
        return new SocialLoginFailureHandler(objectMapper);
    }

    /**
     * CustomUsernamePasswordAuthenticationFilter 빈 등록
     * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
     * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
     * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
     */
    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() {
        CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter(objectMapper);
        customUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());

        return customUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository, userWorkplaceRepository);
    }

    @Bean
    public SocialLoginAuthenticationFilter socialLoginAuthenticationFilter() {
        SocialLoginAuthenticationFilter socialLoginAuthenticationFilter = new SocialLoginAuthenticationFilter(objectMapper, kakaoService, appleService);
        socialLoginAuthenticationFilter.setAuthenticationManager(authenticationManager());
        socialLoginAuthenticationFilter.setAuthenticationSuccessHandler(socialLoginSuccessHandler());
        socialLoginAuthenticationFilter.setAuthenticationFailureHandler(socialLoginFailureHandler());

        return socialLoginAuthenticationFilter;
    }
}
