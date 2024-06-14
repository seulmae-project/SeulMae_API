package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.dto.request.UserSignUpDto;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserSignUpDto userSignUpDto) {
        // 유니크 검사
        checkDuplicatedEmail(userSignUpDto.getEmail());

        if (userRepository.existsByPhoneNumber(userSignUpDto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 존재하는 휴대폰번호입니다.");
        }

        // 휴대폰번호 인증했는가?


        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .phoneNumber(userSignUpDto.getPhoneNumber())
                .password(userSignUpDto.getPassword())
                .name(userSignUpDto.getName())
                .birthday(userSignUpDto.getBirthday())
                .isMale(userSignUpDto.getIsMale())
                .imageURL(userSignUpDto.getImageUrl())
                .authorityRole(Role.USER)
                .build();

        user.encodePassword(passwordEncoder);
        userRepository.save(user);
    }

    public void checkDuplicatedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }
}
