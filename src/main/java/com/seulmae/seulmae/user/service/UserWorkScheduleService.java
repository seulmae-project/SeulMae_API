package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.user.dto.request.UserWorkScheduleAddRequest;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserWorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final UserWorkScheduleRepository userWorkScheduleRepository;
    private final UserRepository userRepository;
    private final UserWorkplaceService userWorkplaceService;

    public void addUserWorkSchedule(UserWorkScheduleAddRequest userWorkScheduleAddRequest, User user) {
        WorkSchedule workSchedule = workScheduleRepository.findById(userWorkScheduleAddRequest.getWorkScheduleId())
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(workSchedule.getWorkplace(), user);

        User targetUser = userRepository.findById(userWorkScheduleAddRequest.getTargetUserId())
                        .orElseThrow(() -> new NoSuchElementException("해당 유저 ID가 존재하지 않습니다."));
        userWorkplaceService.checkWorkplaceAuthority(workSchedule.getWorkplace(), targetUser);


        userWorkScheduleRepository.save(UserWorkSchedule.builder()
                .user(targetUser)
                .workSchedule(workSchedule)
                .build());


    }

    public void deleteUserWorkSchedule(Long userWorkScheduleId, User user) {
        UserWorkSchedule userWorkSchedule = userWorkScheduleRepository.findById(userWorkScheduleId)
                .orElseThrow(() -> new NoSuchElementException("userWorkSchedule ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(userWorkSchedule.getWorkSchedule().getWorkplace(), user);

        userWorkScheduleRepository.delete(userWorkSchedule);
    }
}
