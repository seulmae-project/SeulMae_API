package com.seulmae.seulmae.user;

import com.seulmae.seulmae.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockUserSecurityContextFactory implements WithSecurityContextFactory<MockUser> {
    @Override
    public SecurityContext createSecurityContext(MockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User mockUser = User.builder()
                .idUser(annotation.userId())
                .accountId(annotation.accountId())
                .name(annotation.name())
                .password(annotation.password())
                .phoneNumber(annotation.phoneNumber())
                .isMale(annotation.isMale())
                .birthday(annotation.birthday())
                .authorityRole(annotation.authorityRole())
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, mockUser.getPassword());
        context.setAuthentication(auth);
        return context;
    }
}
