package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.user.dto.request.UserWorkScheduleAddRequest;
import com.seulmae.seulmae.user.dto.request.UserWorkScheduleUpdateRequest;
import com.seulmae.seulmae.user.dto.response.UserWorkScheduleListResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserWorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final UserWorkScheduleRepository userWorkScheduleRepository;
    private final UserRepository userRepository;

    private final UserWorkplaceService userWorkplaceService;
    private final UserService userService;

    public List<UserWorkScheduleListResponse> getUsersByWorkSchedule(Long workScheduleId, User user, HttpServletRequest request) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
        userWorkplaceService.checkManagerAuthority(workSchedule.getWorkplace(), user);

        List<UserWorkSchedule> userWorkSchedules = userWorkScheduleRepository.findAllByWorkSchedule(workSchedule);

        return userWorkSchedules.stream()
                .map(userWorkSchedule -> new UserWorkScheduleListResponse(userWorkSchedule, userService.getUserImageURL(userWorkSchedule.getUser(), request)))
                .toList();

    }

    @Transactional
    public void addUserWorkSchedule(UserWorkScheduleAddRequest userWorkScheduleAddRequest, User user) {
        WorkSchedule workSchedule = workScheduleRepository.findById(userWorkScheduleAddRequest.getWorkScheduleId())
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
        userWorkplaceService.checkManagerAuthority(workSchedule.getWorkplace(), user);

        User targetUser = userRepository.findById(userWorkScheduleAddRequest.getTargetUserId())
                        .orElseThrow(() -> new NoSuchElementException("해당 유저 ID가 존재하지 않습니다."));
        userWorkplaceService.checkWorkplaceAuthority(workSchedule.getWorkplace(), targetUser);


        userWorkScheduleRepository.save(UserWorkSchedule.builder()
                .user(targetUser)
                .workSchedule(workSchedule)
                .build());
    }

    @Transactional
    public void modifyUserWorkSchedule(Long userWorkScheduleId, UserWorkScheduleUpdateRequest request, User user) {
        WorkSchedule workSchedule = workScheduleRepository.findById(request.getWorkScheduleId())
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
        userWorkplaceService.checkManagerAuthority(workSchedule.getWorkplace(), user);

        UserWorkSchedule userWorkSchedule = userWorkScheduleRepository.findById(userWorkScheduleId)
                .orElseThrow(() -> new NoSuchElementException("userWorkSchedule ID가 존재하지 않습니다."));

        userWorkScheduleRepository.save(userWorkSchedule.toBuilder()
                .workSchedule(workSchedule)
                .build());
    }

    @Transactional
    public void deleteUserWorkSchedule(Long userWorkScheduleId, User user) {
        UserWorkSchedule userWorkSchedule = userWorkScheduleRepository.findById(userWorkScheduleId)
                .orElseThrow(() -> new NoSuchElementException("userWorkSchedule ID가 존재하지 않습니다."));
        userWorkplaceService.checkManagerAuthority(userWorkSchedule.getWorkSchedule().getWorkplace(), user);

        userWorkScheduleRepository.delete(userWorkSchedule);
    }
}
