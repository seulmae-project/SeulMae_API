package com.seulmae.seulmae.global.config;

import com.seulmae.seulmae.util.MockSetUpUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@TestConfiguration
public class TestSecurityConfig {
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
                        .requestMatchers(HttpMethod.POST, "/api/users/sms-certification/send").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/sms-certification/confirm").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/id/duplication").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/users/email/search").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/pw").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                        .anyRequest().permitAll()) //여기 부분 다시 고민해보자

                .build();
    }

}
