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
                .orElseThrow(() -> new NoSuchElementException("해당 유저 ID와 근무지 ID와 관련된 UserWorkSchedule이 존재하지 않습니다."));
        WorkplaceJoinHistory workplaceJoinHistory = workplaceJoinHistoryRepository.findByUserAndWorkplace(userWorkplace.getUser(), userWorkplace.getWorkplace())
                .orElseThrow(() -> new NoSuchElementException("해당 유저 ID와 근무지 ID와 관련된 WorkplaceJoinHistory가 존재하지 않습니다."));


        String userImageURL = userService.getUserImageURL(userWorkplace.getUser(), request);

        return new UserInfoWithWorkplaceResponse(userWorkplace, userWorkSchedule, wage, workplaceJoinHistory,userImageURL);
    }

    @Transactional
    public void delegateManagerAuthority(User user, ManagerDelegationRequest managerDelegationRequest) {
        Workplace workplace = findByIdUtil.getWorkplaceById(managerDelegationRequest.getWorkplaceId());
        User delegatee = findByIdUtil.getUserById(managerDelegationRequest.getUserId());

        UserWorkplace delegatorUserWorkplace = userWorkplaceRepository.findByUserAndWorkplace(user, workplace)
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 해당 근무지 소속이 아닙니다."));
        UserWorkplace delegateeUserWorkplace = userWorkplaceRepository.findByUserAndWorkplace(delegatee, workplace)
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
        return userWorkplaceRepository.existsByWorkplaceAndUserAndIsManager(workplace, user, true);
    }

    public boolean isAssociated(Workplace workplace, User user) {
        return userWorkplaceRepository.existsByWorkplaceAndUser(workplace, user);
    }


}
