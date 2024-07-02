package com.seulmae.seulmae.util;

import com.seulmae.seulmae.user.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockUserSecurityContextFactory.class)
public @interface MockUser {
    long userId() default 1L;
    String accountId() default "test1234";
    String phoneNumber() default "01024231234";
    String password() default "qwer1234!";
    String name() default "이름";
    String birthday() default "19920103";
    boolean isMale() default true;
    Role authorityRole() default Role.USER;
    boolean isDelUser() default false;
}
