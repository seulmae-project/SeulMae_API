package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FindByIdUtil;
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
import com.seulmae.seulmae.global.exception.InvalidAccountIdException;
import com.seulmae.seulmae.global.exception.InvalidPasswordException;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final FindByIdUtil findByIdUtil;

    private final UserImageService userImageService;
    private final SmsService smsService;

    private final String FILE_ENDPOINT = "/api/users/file";
    private final String SIGNUP = "signUp";
    private final String FIND_ACCOUNT_ID = "findAccountId";
    private final String FIND_PW = "findPassword";

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
            userImageService.createUserImage(file, user);
        }
    }

    @Transactional
    public void updateUser(Long id, User user, UpdateUserRequest updateUserRequest, MultipartFile file) throws AccessDeniedException {
        if (id != user.getIdUser()) {
            throw new AccessDeniedException("프로필을 수정할 권한이 없습니다.");
        }

        User targetUser = findByIdUtil.getUserById(user.getIdUser());
        targetUser.updateName(updateUserRequest.getName());

        if (file != null && !file.isEmpty()) {
            userImageService.updateUserImage(file, targetUser);
        }

        userRepository.save(targetUser);
    }


    /**
     * ROLE UPDATE: GUEST -> USER
     */
    @Transactional
    public void updateAdditionalProfile(String socialId, SocialType socialType, OAuth2AdditionalDataRequest request, MultipartFile file) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));
        user.updateAdditionalInfo(request.getName(), request.getIsMale(), request.getBirthday());
        user.authorizeUser();

        if (file != null && !file.isEmpty()) {
            userImageService.updateUserImage(file, user);
        }

        userRepository.save(user);
    }

    public FindAuthResponse getAccountId(String phoneNumber) {
        String rePhoneNumber = phoneNumber.replace("-", "");
        User user = userRepository.findByPhoneNumber(rePhoneNumber)
                .orElseThrow(() -> new NoSuchElementException("해당 휴대폰번호로 가입한 이력이 없습니다."));
        return new FindAuthResponse(true, user.getAccountId());
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
            throw new AccessDeniedException("앱을 탈퇴할 권한이 없습니다.");
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
                .ifPresent(UserImage::delete);

        userRepository.save(user);
        /**
         * [TODO]
         * 1. 추후에 탈퇴할 경우, del처리 해야할 부분 있으면 추가해야 함.
         * 2. del 처리할 경우, 로그인이나 기타 처리가 되지 않아야 함.
         */

    }

    public String getUserImageURL(User user, HttpServletRequest request) {
        if (user.getUserImage() != null) {
            Long userImageId = user.getUserImage().getIdUserImage();
            return userImageId != null ? UrlUtil.getBaseUrl(request) + FILE_ENDPOINT + "?userImageId=" + userImageId : null;
        }
        return null;
    }

    public UserProfileResponse getUserProfile(Long id, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 UserId가 존재하지 않습니다."));
        List<Workplace> workplaces = userWorkplaceRepository.findWorkplacesByUser(user);

        List<UserWorkplaceInfoResponse> userWorkplaceInfoResponses = new ArrayList<>();

        for (Workplace workplace : workplaces) {
            UserWorkplace userWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(user, workplace)
                            .orElseThrow(() -> new NoSuchElementException("해당 유저는 해당 근무지 소속이 아닙니다."));

            User manger = userWorkplaceRepository.findUserByWorkplaceAndIsManager(workplace, true)
                            .orElseThrow(() -> new NoSuchElementException("해당 근무지에 매니저가 존재하지 않습니다."));

            userWorkplaceInfoResponses.add(new UserWorkplaceInfoResponse(workplace.getIdWorkPlace(), workplace.getWorkplaceName(), workplace.getAddressVo(), workplace.getWorkplaceName() , manger.getName(),  userWorkplace.getIsManager()));
        }

        return new UserProfileResponse(user.getName(), getUserImageURL(user, request), user.getPhoneNumber(), userWorkplaceInfoResponses);
    }

    public UserProfileResponse getMyProfile(User user, HttpServletRequest request) {
        User me = userRepository.findById(user.getIdUser())
                .orElseThrow(() -> new NoSuchElementException("해당 User가 존재하지 않습니다."));

        List<Workplace> workplaces = userWorkplaceRepository.findWorkplacesByUser(me);

        List<UserWorkplaceInfoResponse> userWorkplaceInfoResponses = new ArrayList<>();

        for (Workplace workplace : workplaces) {
            UserWorkplace userWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(user, workplace)
                    .orElseThrow(() -> new NoSuchElementException("해당 유저는 해당 근무지 소속이 아닙니다."));

            User manger = userWorkplaceRepository.findUserByWorkplaceAndIsManager(workplace, true)
                    .orElseThrow(() -> new NoSuchElementException("해당 근무지에 매니저가 존재하지 않습니다."));

            userWorkplaceInfoResponses.add(new UserWorkplaceInfoResponse(workplace.getIdWorkPlace(), workplace.getWorkplaceName(), workplace.getAddressVo(), workplace.getWorkplaceName() , manger.getName(),  userWorkplace.getIsManager()));
        }

        return new UserProfileResponse(me.getName(), getUserImageURL(me, request), me.getPhoneNumber(), userWorkplaceInfoResponses);
    }

    /**
     * sms 보내기 서비스 (보내기 여부를 판단하는 서비스)
     * 0. 보내는 상황
     * - 회원가입
     *   - 기존 db에 유저가 존재해서는 안된다.
     *   - 기존 db에 유저가 존재하는 경우, '해당 휴대폰번호로 가입한 이력이 있습니다. 아이디 찾기를 이용하시기 바랍니다.'
     * - 아이디 찾기
     *   - 기존 db에 유저가 존재해야 한다.
     *   - 기존 db에 휴대폰 번호에 대한 유저가 존재하지 않을 경우, sms를 보내면 안된다.
     * - 비밀번호 찾기
     *   - 아이디와 휴대폰번호가 동시에 존재하는 유저가 db에 존재해야 한다.
     *   - 만약 일치하는 데이터가 없을 경우, sms를 보내면 안된다.
     */
    public FindAuthResponse sendSMSCertification(SmsSendingRequest request) {
        /**
         * 회원가입
         */
        if (request.getSendingType().equals(SIGNUP)) {

            checkDuplicatedPhoneNumber(request.getPhoneNumber());

            smsService.sendSMS(request.getPhoneNumber());

            return new FindAuthResponse(true);
        }

        /**
         * 아이디 찾기
         */
        if (request.getSendingType().equals(FIND_ACCOUNT_ID)) {
            if (!isDuplicatedPhoneNumber(request.getPhoneNumber())) {
                throw new IllegalArgumentException("가입된 휴대폰 번호가 아닙니다.");
            }

            smsService.sendSMS(request.getPhoneNumber());

            return new FindAuthResponse(true);
        }

        /**
         * 비밀번호 찾기
         */
        if (request.getSendingType().equals(FIND_PW)) {
            if (!userRepository.existsByAccountIdAndPhoneNumber(request.getAccountId(), request.getPhoneNumber())) {
                throw new IllegalArgumentException("해당 아이디와 휴대폰번호가 매칭되는 계정이 존재하지 않습니다.");
            }

            smsService.sendSMS(request.getPhoneNumber());

            return new FindAuthResponse(true);
        }

        throw new IllegalArgumentException(request.getSendingType() + " 타입은 존재하지 않는 타입입니다.");
    }

    /**
     * sms 인증 서비스 (종류에 따라 제공하는 리스폰스가 달라진다.)
     * 1. 회원가입
     * - 성공/실패 리스폰스
     *
     * 2. 아이디 찾기
     * - 성공/실패 리스폰스
     * - 아이디(accountId)
     *
     * 3. 비밀번호 찾기
     * - 성공/실패 리스폰스
     */

    public FindAuthResponse confirmSMSCertification(SmsCertificationRequest request) {
        /**
         * 회원가입 & 비밀번호 찾기
         */
        if (request.getSendingType().equals(SIGNUP) || request.getSendingType().equals(FIND_PW)) {

            smsService.verifySMS(request);

            return new FindAuthResponse(true);
        }

        /**
         * 아이디 찾기
         */
        if (request.getSendingType().equals(FIND_ACCOUNT_ID)) {
            smsService.verifySMS(request);

            return getAccountId(request.getPhoneNumber());
        }

        throw new IllegalArgumentException(request.getSendingType() + " 타입은 존재하지 않는 타입입니다.");

    }
}
