package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserWorkplaceService {
    private final UserWorkplaceRepository userWorkplaceRepository;

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
