package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.PasswordUtil;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.OAuth2AdditionalDataRequest;
import com.seulmae.seulmae.user.dto.request.ChangePasswordRequest;
import com.seulmae.seulmae.user.dto.request.UpdateUserRequest;
import com.seulmae.seulmae.user.dto.request.UserSignUpDto;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.exception.InvalidPasswordException;
import com.seulmae.seulmae.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserSignUpDto userSignUpDto) {

        checkDuplicatedEmail(userSignUpDto.getEmail());
        checkDuplicatedPhoneNumber(userSignUpDto.getPhoneNumber());
        checkPasswordValidation(userSignUpDto.getPassword());

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

    @Transactional
    public void updateUser(Long id, Long loginId, UpdateUserRequest request) throws AccessDeniedException {
        if (id != loginId) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("대상 유저를 찾을 수 없습니다."));

        targetUser.updateName(request.getName());
        targetUser.updateImageUrl(request.getImageUrl());
        userRepository.save(targetUser);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * ROLE UPDATE: GUEST -> USER
     */
    @Transactional
    public void updateAdditionalProfile(String socialId, SocialType socialType, OAuth2AdditionalDataRequest request) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));
        user.updateAdditionalInfo(request.getName(), request.getIsMale(), request.getBirthday(), request.getImageURL());
        user.authorizeUser();
        userRepository.save(user);
    }

    public FindAuthResponse getEmail(String phoneNumber) {
        String rePhoneNumber = phoneNumber.replace("-", "");
        User user = userRepository.findByPhoneNumber(rePhoneNumber)
                .orElse(null);
        return new FindAuthResponse(user == null ? null : user.getEmail());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        checkPasswordValidation(request.getPassword());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 이메일과 일치하는 유저가 존재하지 않습니다."));

        user.changePassword(passwordEncoder, request.getPassword());
        userRepository.save(user);
    }


    public void checkDuplicatedEmail(String email) {
        if (isDuplicatedEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    public void checkDuplicatedPhoneNumber(String phoneNumber) {
        if (isDuplicatedPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("이미 존재하는 휴대폰번호입니다.");
        }
    }

    public void checkPasswordValidation(String password) {
        if (!PasswordUtil.isValidPassword(password)) {
            throw new InvalidPasswordException("비밀번호로 영문, 숫자, 특수문자 포함 8자 이상을 입력해주세요.");
        }
    }

    public boolean isDuplicatedEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isDuplicatedPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
}
