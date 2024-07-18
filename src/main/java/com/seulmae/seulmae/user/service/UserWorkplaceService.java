package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.user.dto.response.UserInfoWithWorkplaceResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.wage.repository.WageRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserWorkplaceService {
    private final UserWorkplaceRepository userWorkplaceRepository;
    private final WageRepository wageRepository;
    private final UserWorkScheduleRepository userWorkScheduleRepository;

    private final FindByIdUtil findByIdUtil;

    private final UserService userService;

    public UserInfoWithWorkplaceResponse getUserInfoByUserWorkplace(Long userWorkplaceId, HttpServletRequest request) {
        UserWorkplace userWorkplace = findByIdUtil.getUserWorkplaceById(userWorkplaceId);
        Wage wage = wageRepository.findByUserAndWorkplace(userWorkplace.getUser(), userWorkplace.getWorkplace())
                .orElseThrow(() -> new NoSuchElementException("해당 유저 ID와 근무지 ID와 관련된 Wage가 존재하지 않습니다."));
        UserWorkSchedule userWorkSchedule = userWorkScheduleRepository.findByUserAndWorkplace(userWorkplace.getUser(), userWorkplace.getWorkplace())
                .orElseThrow(() -> new NoSuchElementException("해당 유저 ID와 근무지 ID와 관련된 UserWorkSchedule이 존재하지 않습니다."));

        String userImageURL = userService.getUserImageURL(userWorkplace.getUser(), request);

        return new UserInfoWithWorkplaceResponse(userWorkplace, userWorkSchedule, wage, userImageURL);
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
