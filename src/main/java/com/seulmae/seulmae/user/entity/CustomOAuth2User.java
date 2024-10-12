//package com.seulmae.seulmae.user.entity;
//
//import com.seulmae.seulmae.user.enums.Role;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//
//import java.util.Collection;
//import java.util.Map;
//
//
///**
// * DefaultOAuth2User를 상속하고, aacountId와 role 필드를 추가로 가진다.
// */
//@Getter
//public class CustomOAuth2User extends DefaultOAuth2User {
//    private String accountId;
//    private Role role;
//
//    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, String accountId, Role role) {
//        super(authorities, attributes, nameAttributeKey);
//        this.accountId = accountId;
//        this.role = role;
//    }
//}
