package com.seulmae.seulmae.global.util;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.wage.repository.WageRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FindByUserAndWorkPlaceUtil {
    private final UserRepository userRepository;
    private final WorkplaceRepository workplaceRepository;

    private WageRepository wageRepository;

    public Wage getWageByUserAndWorkPlace(User user, Workplace workplace) {
        return wageRepository.findByUserAndWorkplace(user, workplace)
                .orElseThrow(() -> new NoSuchElementException("해당 월급정보가 존재하지 않습니다."));
    }
}
