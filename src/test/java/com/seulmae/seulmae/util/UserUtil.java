package com.seulmae.seulmae.util;

import com.seulmae.seulmae.user.enums.Role;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    @Autowired
    private UserRepository userRepository;


    public User createDefaultTestUserAndLogin(String accountId, String password, String phoneNumber, String userName) {
        return createTestUserAndLogin(accountId, password, phoneNumber, userName, Role.USER);
    }

    public User createDefaultTestUser(String accountId, String password, String phoneNumber, String userName) {
        return createTestUser(accountId, password, phoneNumber, userName, Role.USER);
    }

    /** 유틸리티 메서드: 동적으로 User 생성 및 로그인 **/
    public User createTestUserAndLogin(String accountId, String password, String phoneNumber, String name, Role role) {
        User user = userRepository.save(User.builder()
                .accountId(accountId)
                .password(password)
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(true)
                .birthday("19920103")
                .authorityRole(role)
                .build());

        // Authentication 설정
        setAuthentication(user);

        return user;
    }

    /** 유틸리티 메서드: 동적으로 User 생성 **/
    public User createTestUser(String accountId, String password, String phoneNumber, String name, Role role) {
        return userRepository.save(User.builder()
                .accountId(accountId)
                .password(password)
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(true)
                .birthday("19920103")
                .authorityRole(role)
                .build());
    }

    public void loginTestUser(String accountId) {
        User user = userRepository.findByAccountId(accountId).orElseThrow(() -> new NullPointerException("존재하지 않는 사용자 아이디입니다."));

        setAuthentication(user);
    }

    public void setAuthentication(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), AuthorityUtils.createAuthorityList(String.valueOf(user.getAuthorityRole())));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
