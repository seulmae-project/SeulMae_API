package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.global.util.PasswordUtil;
import com.seulmae.seulmae.global.util.UrlUtil;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.*;
import com.seulmae.seulmae.user.dto.response.FindAuthResponse;
import com.seulmae.seulmae.user.dto.response.UserProfileResponse;
import com.seulmae.seulmae.user.dto.response.UserWorkplaceInfoResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.exception.InvalidAccountIdException;
import com.seulmae.seulmae.user.exception.InvalidPasswordException;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

    private final PasswordEncoder passwordEncoder;

    private final String FILE_ENDPOINT = "/api/users/file";

    @Transactional
    public void createUser(UserSignUpDto userSignUpDto, MultipartFile file) {

        checkDuplicatedAccountId(userSignUpDto.getAccountId());
        checkDuplicatedPhoneNumber(userSignUpDto.getPhoneNumber());
        checkPasswordValidation(userSignUpDto.getPassword());
        checkAccountIdValidation(userSignUpDto.getAccountId());

        User user = User.builder()
                .accountId(userSignUpDto.getAccountId())
                .phoneNumber(userSignUpDto.getPhoneNumber())
                .password(userSignUpDto.getPassword())
                .name(userSignUpDto.getName())
                .birthday(userSignUpDto.getBirthday())
                .isMale(userSignUpDto.getIsMale())
                .authorityRole(Role.USER)
                .build();

        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        if (file != null && !file.isEmpty()) {
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
    public void updateUser(Long id, User user, UpdateUserRequest updateUserRequest, MultipartFile file) throws AccessDeniedException {
        if (id != user.getIdUser()) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }

//        User targetUser = userRepository.findById(loginId)
//                .orElseThrow(() -> new NoSuchElementException("대상 유저를 찾을 수 없습니다."));

        user.updateName(updateUserRequest.getName());

        if (file != null && !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String filePath = "C:\\Users\\hany\\uploads\\users\\" + user.getIdUser();
                userImageRepository.findByUser(user)
                        .ifPresentOrElse(userImage -> userImage.update(fileName, filePath, FileUtil.getFileExtension(file)),
                                () -> {
                                    UserImage newUserImage = new UserImage(user, fileName, filePath, FileUtil.getFileExtension(file));
                                    user.updateUserImage(newUserImage);
//                                    userRepository.save(targetUser);
                                });

                // TODO: 서버에 저장된 기존 사진은 어떻게 할 것인가? 지우기 VS 남겨두기
                FileUtil.uploadFile(filePath, fileName, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userRepository.save(user);
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

        if (file != null && !file.isEmpty()) {
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

    public FindAuthResponse getAccountId(String phoneNumber) {
        String rePhoneNumber = phoneNumber.replace("-", "");
        User user = userRepository.findByPhoneNumber(rePhoneNumber)
                .orElse(null);
        return new FindAuthResponse(user == null ? null : user.getAccountId());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        checkPasswordValidation(request.getPassword());

        User user = userRepository.findByAccountId(request.getAccountId())
                .orElseThrow(() -> new NoSuchElementException("해당 아이디와 일치하는 유저가 존재하지 않습니다."));

        user.changePassword(passwordEncoder, request.getPassword());
        userRepository.save(user);
    }


    public void checkDuplicatedAccountId(String accountId) {
        if (isDuplicatedAccountId(accountId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
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

    public void checkAccountIdValidation(String accountId) {
        if (!isCorrectAccountId(accountId)) {
            throw new InvalidAccountIdException("아이디로 영문 또는 숫자 5자 이상을 입력해주세요.");
        }
    }

    public boolean isDuplicatedAccountId(String accountId) {
        return userRepository.existsByAccountId(accountId);
    }

    public boolean isDuplicatedPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean isCorrectAccountId(String accountId) {
            // 길이 체크
            if (accountId.length() < 5) {
                return false;
            }

            // 영어와 숫자로만 이루어져 있는지 체크 (^[a-zA-Z0-9]*$)
            if (!accountId.matches("^[a-zA-Z0-9]*$")) {
                return false;
            }

            return true;
    }

    @Transactional
    public void changePhoneNumber(Long id, ChangePhoneNumberRequest request, User user) throws AccessDeniedException {
        if (!id.equals(user.getIdUser())) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }
        checkDuplicatedPhoneNumber(request.getPhoneNumber());
        user.updatePhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id, User user) throws AccessDeniedException {
        if (!id.equals(user.getIdUser())) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }
        user.deleteUser();
        userImageRepository.findByUser(user)
                .ifPresentOrElse(UserImage::delete, null);
        /**
         * [TODO]
         * 1. 추후에 탈퇴할 경우, del처리 해야할 부분 있으면 추가해야 함.
         * 2. del 처리할 경우, 로그인이나 기타 처리가 되지 않아야 함.
         */

    }

    public String getUserImageURL(User user, HttpServletRequest request) {
        Long userImageId = user.getUserImage().getIdUserImage();
        return userImageId != null ? UrlUtil.getBaseUrl(request) + FILE_ENDPOINT + "?userImageId=" + userImageId : null;
    }

    public UserProfileResponse getUserProfile(Long id, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 UserId가 존재하지 않습니다."));
        List<Workplace> workplaces = userWorkplaceRepository.findWorkplacesByUser(user);

        List<UserWorkplaceInfoResponse> userWorkplaceInfoResponses = new ArrayList<>();

        for (Workplace workplace : workplaces) {
            userWorkplaceInfoResponses.add(new UserWorkplaceInfoResponse(workplace.getWorkplaceName(), workplace.getAddressVo()));
        }

        return new UserProfileResponse(user.getName(),getUserImageURL(user, request), user.getPhoneNumber(), userWorkplaceInfoResponses);
    }

}
