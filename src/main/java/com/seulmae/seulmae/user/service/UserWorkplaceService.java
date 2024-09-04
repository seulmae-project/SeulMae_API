package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.global.util.UrlUtil;
import com.seulmae.seulmae.user.dto.request.ManagerDelegationRequest;
import com.seulmae.seulmae.user.dto.response.UserInfoWithWorkplaceResponse;
import com.seulmae.seulmae.user.dto.response.UserWorkplaceUserResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.wage.repository.WageRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserWorkplaceService {
    private final UserWorkplaceRepository userWorkplaceRepository;
    private final WageRepository wageRepository;
    private final UserWorkScheduleRepository userWorkScheduleRepository;
    private final WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;

    private final FindByIdUtil findByIdUtil;

    private final UserService userService;

    @Value("${file.endPoint.user}")
    private String userImageEndPoint;

    public UserInfoWithWorkplaceResponse getUserInfoByUserWorkplace(Long userWorkplaceId, HttpServletRequest request) {
        UserWorkplace userWorkplace = findByIdUtil.getUserWorkplaceById(userWorkplaceId);
        Wage wage = wageRepository.findByUserAndWorkplace(userWorkplace.getUser(), userWorkplace.getWorkplace())
                .orElseThrow(() -> new NoSuchElementException("해당 유저 ID와 근무지 ID와 관련된 Wage가 존재하지 않습니다."));
        UserWorkSchedule userWorkSchedule = userWorkScheduleRepository.findByUserAndWorkplace(userWorkplace.getUser(), userWorkplace.getWorkplace())
                .orElse(null);
//        UserWorkSchedule userWorkSchedule = userWorkScheduleRepository.findByUserAndWorkplace(userWorkplace.getUser(), userWorkplace.getWorkplace())
//                .orElseThrow(() -> new NoSuchElementException("해당 유저 ID와 근무지 ID와 관련된 UserWorkSchedule이 존재하지 않습니다."));
        WorkplaceJoinHistory workplaceJoinHistory = workplaceJoinHistoryRepository.findByUserAndWorkplaceAndIsApproveTrue(userWorkplace.getUser(), userWorkplace.getWorkplace())
                .orElseThrow(() -> new NoSuchElementException("해당 유저 ID와 근무지 ID와 관련된 WorkplaceJoinHistory가 존재하지 않습니다."));


        String userImageURL = userService.getUserImageURL(userWorkplace.getUser(), request);

        return new UserInfoWithWorkplaceResponse(userWorkplace, userWorkSchedule, wage, workplaceJoinHistory,userImageURL);
    }

    @Transactional
    public void delegateManagerAuthority(User user, ManagerDelegationRequest managerDelegationRequest) {
        Workplace workplace = findByIdUtil.getWorkplaceById(managerDelegationRequest.getWorkplaceId());
        User delegatee = findByIdUtil.getUserById(managerDelegationRequest.getUserId());

        UserWorkplace delegatorUserWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(user, workplace)
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 해당 근무지 소속이 아닙니다."));
        UserWorkplace delegateeUserWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(delegatee, workplace)
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 해당 근무지 소속이 아닙니다."));

        delegateeUserWorkplace.setIsManagerTrue();
        delegatorUserWorkplace.setIsManagerFalse();

        userWorkplaceRepository.saveAll(Arrays.asList(delegatorUserWorkplace, delegateeUserWorkplace));
    }

    @Transactional
    public List<UserWorkplaceUserResponse> getAllUserFromWorkplace(Long workplaceId, HttpServletRequest httpServletRequest) {
        List<UserWorkplace> userWorkplaceList = userWorkplaceRepository.findByWorkplaceId(workplaceId);

        List<UserWorkplaceUserResponse> userWorkplaceUserResponseList = userWorkplaceList.stream().parallel()
                .map(userWorkplace -> {

                    Long userId = userWorkplace.getUser().getIdUser();
                    String userName = userWorkplace.getUser().getName();

                    UserImage userImage = userWorkplace.getUser().getUserImage();
                    String userImageUrl = userImage != null ? UrlUtil.getBaseUrl(httpServletRequest) + userImageEndPoint + "?userImageId=" + userImage.getIdUserImage() : null;

                    return UserWorkplaceUserResponse.builder()
                            .userId(userId)
                            .userName(userName)
                            .userImageUrl(userImageUrl)
                            .isManager(userWorkplace.getIsManager())
                            .build();
                })
                .toList();

        return userWorkplaceUserResponseList;
    }

    public void checkMangerAuthority(Workplace workplace, User user) {
        if (!isManager(workplace, user)) {
            throw new IllegalArgumentException("해당 근무지의 매니저가 아닙니다.");
        }
    }

    public void checkWorkplaceAuthority(Workplace workplace, User user) {
        if (!isAssociated(workplace, user)) {
            throw new IllegalArgumentException("해당 근무지 소속이 아닙니다.");
        }
    }

    public boolean isManager(Workplace workplace, User user) {
        return userWorkplaceRepository.existsByWorkplaceAndUserAndIsManagerAndIsDelUserWorkplaceFalse(workplace, user, true);
    }

    public boolean isAssociated(Workplace workplace, User user) {
        return userWorkplaceRepository.existsByWorkplaceAndUserAndIsDelUserWorkplaceFalse(workplace, user);
    }

    @Transactional
    public void withdrawWorkplace(User user, Long workplaceId) {
        // 해당 유저의 아이디와 workplaceId를 가진 userWorkplace가 존재하는지 확인
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);
        UserWorkplace userWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(user, workplace)
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 현재 해당 근무지에 소속되지 않았습니다."));

        // 이미 true인 경우, 기존에 이미 삭제된 경우라고 말하기
        if (userWorkplace.getIsDelUserWorkplace()) {
            throw new IllegalStateException("이미 탈퇴한 근무지입니다.");
        }
        // 만약 매니저인 경우, 이양하고 삭제할 수 있게 에러를 띄우기
        if (isManager(workplace, user)) {
            throw new IllegalStateException("매니저 권한을 이양하고 탈퇴하시기 바랍니다.");
        }

        // 존재하고 isDel이 false인 경우만, true하기
        userWorkplace.deleteUserWorkplace();
        userWorkplaceRepository.save(userWorkplace);

    }
}
