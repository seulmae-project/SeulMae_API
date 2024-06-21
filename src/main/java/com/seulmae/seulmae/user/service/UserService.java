package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.global.util.PasswordUtil;
import com.seulmae.seulmae.global.util.UrlUtil;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.dto.response.UserProfileResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.exception.InvalidPasswordException;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;

    private final PasswordEncoder passwordEncoder;

    private final String FILE_ENDPOINT = "/api/users/file";

    @Transactional
    public void createUser(UserSignUpDto userSignUpDto, MultipartFile file) {

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
                .authorityRole(Role.USER)
                .build();

        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        if (file != null & !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String filePath = "C:\\Users\\hany\\uploads\\users\\" + user.getIdUser();
                UserImage userImage = new UserImage(user, fileName, filePath, FileUtil.getFileExtension(file));
                user.updateUserImage(userImage);
                FileUtil.uploadFile(filePath, fileName, file);
                userRepository.save(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Transactional
    public void updateUser(Long id, Long loginId, UpdateUserRequest updateUserRequest, MultipartFile file) throws AccessDeniedException {
        if (id != loginId) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("대상 유저를 찾을 수 없습니다."));

        targetUser.updateName(updateUserRequest.getName());

        if (file != null & !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String filePath = "C:\\Users\\hany\\uploads\\users\\" + targetUser.getIdUser();
                userImageRepository.findByUser(targetUser)
                        .ifPresentOrElse(userImage -> userImage.update(fileName, filePath, FileUtil.getFileExtension(file)),
                                () -> {
                                    UserImage newUserImage = new UserImage(targetUser, fileName, filePath, FileUtil.getFileExtension(file));
                                    targetUser.updateUserImage(newUserImage);
                                    userRepository.save(targetUser);
                                });

                // TODO: 서버에 저장된 기존 사진은 어떻게 할 것인가? 지우기 VS 남겨두기
                FileUtil.uploadFile(filePath, fileName, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
//    }

    /**
     * ROLE UPDATE: GUEST -> USER
     */
    @Transactional
    public void updateAdditionalProfile(String socialId, SocialType socialType, OAuth2AdditionalDataRequest request, MultipartFile file) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));
        user.updateAdditionalInfo(request.getName(), request.getIsMale(), request.getBirthday());
        user.authorizeUser();
//        userRepository.save(user);

        if (file != null & !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String filePath = "C:\\Users\\hany\\uploads\\users\\" + user.getIdUser();

                userImageRepository.findByUser(user)
                        .ifPresentOrElse(_userImage ->
                                        _userImage.update(fileName, filePath, FileUtil.getFileExtension(file)),
                                () -> {
                                    UserImage newUserImage = new UserImage(user, fileName, filePath, FileUtil.getFileExtension(file));
                                    user.updateUserImage(newUserImage);
                                    userRepository.save(user);
                                });
                // TODO: 서버에 저장된 기존 사진은 어떻게 할 것인가? 지우기 VS 남겨두기
                FileUtil.uploadFile(filePath, fileName, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    @Transactional
    public void changePhoneNumber(Long id, ChangePhoneNumberRequest request, User user) throws AccessDeniedException {
        if (!id.equals(user.getIdUser())) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }
        checkDuplicatedPhoneNumber(request.getPhoneNumber());
        user.updatePhoneNumber(request.getPhoneNumber());
    }

    @Transactional
    public void deleteUser(Long id, User user) throws AccessDeniedException {
        if (!id.equals(user.getIdUser())) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }
        user.deleteUser();
        userImageRepository.findByUser(user)
                .ifPresentOrElse(UserImage::delete, null);

    }

    public String getUserImageURL(User user, HttpServletRequest request) {
        Long userImageId = user.getUserImage().getIdUserImage();
        return userImageId != null ? UrlUtil.getBaseUrl(request) + FILE_ENDPOINT + "?userImageId=" + userImageId : null;
    }

    public UserProfileResponse getUserProfile(Long id, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 UserId가 존재하지 않습니다."));
        return new UserProfileResponse(user.getName(), getUserImageURL(user, request));
    }

}
