package com.seulmae.seulmae.workplace.service;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.service.UserWorkplaceService;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.dto.WorkScheduleAddDto;
import com.seulmae.seulmae.workplace.dto.WorkScheduleInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkScheduleUpdateDto;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final WorkplaceRepository workplaceRepository;
    private final WorkScheduleRepository workScheduleRepository;

    private final UserWorkplaceService userWorkplaceService;

    public WorkScheduleInfoDto getWorkSchedule(Long workScheduleId, User user) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(workSchedule.getWorkplace(), user);

        return new WorkScheduleInfoDto(workSchedule);
    }

    public List<WorkScheduleInfoDto> getWorkSchedules(Long workplaceId, User user) {
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무지 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(workplace, user);

        return workScheduleRepository.findAllByWorkplace(workplace).stream()
                .sorted((ws1, ws2) -> ws2.getRegDateWorkSchedule().compareTo(ws1.getRegDateWorkSchedule()))
                .map(workSchedule -> getWorkSchedule(workSchedule.getIdWorkSchedule(), user))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createWorkSchedule(WorkScheduleAddDto request, User user) {
        // 매니저 권한이 있는가?
        Workplace workplace = workplaceRepository.findById(request.getWorkplaceId())
                .orElseThrow(() -> new NoSuchElementException("해당 근무지 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(workplace, user);

        // workSchedule 생성
        WorkSchedule workSchedule = WorkSchedule.builder()
                .workScheduleTitle(request.getWorkScheduleTitle())
                .workplace(workplace)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        // workScheduleDay 생성
        List<WorkScheduleDay> workScheduleDays = request.getDays().stream()
                .map(dayInt -> {
                    Day day = Day.fromInt(dayInt);
                    return WorkScheduleDay.builder()
                            .workSchedule(workSchedule)
                            .day(day)
                            .build();
                }).toList();

        workSchedule.setWorkScheduleDays(workScheduleDays);
        workScheduleRepository.save(workSchedule);
    }

    public void updateWorkSchedule(Long workScheduleId, WorkScheduleUpdateDto request, User user) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(workSchedule.getWorkplace(), user);

        WorkSchedule updatedWorkSchedule = workSchedule.toBuilder()
                .workScheduleTitle(request.getWorkScheduleTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(request.getIsActive())
                .build();

        List<WorkScheduleDay> workScheduleDays = request.getDays().stream()
                .map(dayInt -> {
                    Day day = Day.fromInt(dayInt);
                    return WorkScheduleDay.builder()
                            .workSchedule(updatedWorkSchedule)
                            .day(day)
                            .build();
                }).toList();

        updatedWorkSchedule.setWorkScheduleDays(workScheduleDays);
        workScheduleRepository.save(updatedWorkSchedule);
    }

    public void deleteWorkSchedule(Long workScheduleId, User user) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(workSchedule.getWorkplace(), user);

        workScheduleRepository.deleteById(workScheduleId);
    }
}
