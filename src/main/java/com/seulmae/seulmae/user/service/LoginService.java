package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디가 존재하지 않습니다."));

        if (isDeletedUser(accountId)) {
            throw new NoSuchElementException("탈퇴한 유저입니다");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getAccountId())
                .password(user.getPassword())
                .roles(user.getAuthorityRole().name())
                .build();
    }

    private boolean isDeletedUser(String accountId) {
        return userRepository.existsByAccountIdAndIsDelUserTrue(accountId);
    }
}
