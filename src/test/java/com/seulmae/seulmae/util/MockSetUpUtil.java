package com.seulmae.seulmae.util;

import com.seulmae.seulmae.global.util.UUIDUtil;
import com.seulmae.seulmae.user.Role;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.wage.repository.WageRepository;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import com.seulmae.seulmae.workplace.vo.AddressVo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MockSetUpUtil {

    private final UserRepository userRepository;
    private final WorkplaceRepository workplaceRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;
    private final UserWorkScheduleRepository userWorkScheduleRepository;
    private final WageRepository wageRepository;
    private final WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;

    public MockSetUpUtil(UserRepository userRepository, WorkplaceRepository workplaceRepository, WorkScheduleRepository workScheduleRepository, UserWorkplaceRepository userWorkplaceRepository, UserWorkScheduleRepository userWorkScheduleRepository, WageRepository wageRepository, WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository) {
        this.userRepository = userRepository;
        this.workplaceRepository = workplaceRepository;
        this.workScheduleRepository = workScheduleRepository;
        this.userWorkplaceRepository = userWorkplaceRepository;
        this.userWorkScheduleRepository = userWorkScheduleRepository;
        this.wageRepository = wageRepository;
        this.workplaceJoinHistoryRepository = workplaceJoinHistoryRepository;
    }

    public User createUser(String accountId, String password, String phoneNumber, String name, String birthday, Boolean isMale) {
        return userRepository.saveAndFlush(User.builder()
                .accountId(accountId)
                .password(password)
                .phoneNumber(phoneNumber)
                .name(name)
                .isMale(isMale)
                .birthday(birthday)
                .authorityRole(Role.USER)
                .build());
    }

    public User creatSocialUser(String accountId, String password, String socialId, SocialType socialType) {
        return userRepository.saveAndFlush(User.builder()
                .accountId(accountId)
                .password(password)
                .socialId(socialId)
                .socialType(socialType)
                .authorityRole(Role.GUEST)
                .build());
    }

    public Workplace createWorkplace(String workplaceName, String mainAddress, String subAddress, String workplaceTel) {
        String topic = "workplace" + UUID.randomUUID().toString().replace("-", "");

        AddressVo addressVo = AddressVo.builder()
                .mainAddress(mainAddress)
                .subAddress(subAddress)
                .build();

        return workplaceRepository.saveAndFlush(Workplace.builder()
                .workplaceCode(UUIDUtil.generateShortUUID())
                .workplaceTopic(topic)
                .workplaceName(workplaceName)
                .addressVo(addressVo)
                .workplaceTel(workplaceTel)
                .build());
    }

    public WorkSchedule createWorkSchedule(Workplace workplace, String workScheduleTitle, LocalTime startTime, LocalTime endTime, List<Integer> days) {
        WorkSchedule workSchedule = WorkSchedule.builder()
                .workScheduleTitle(workScheduleTitle)
                .workplace(workplace)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        List<WorkScheduleDay> workScheduleDays = days.stream()
                .map(dayInt -> {
                    Day day = Day.fromInt(dayInt);
                    return WorkScheduleDay.builder()
                            .workSchedule(workSchedule)
                            .day(day)
                            .build();
                }).collect(Collectors.toList());

        workSchedule.setWorkScheduleDays(workScheduleDays);

        return workScheduleRepository.saveAndFlush(workSchedule);

    }

    public UserWorkplace createUserWorkplace(User targetUser, Workplace workplace, Boolean isManager) {
        return userWorkplaceRepository.saveAndFlush(UserWorkplace.builder()
                .user(targetUser)
                .workplace(workplace)
                .isManager(isManager)
                .build());
    }

    public Wage createWage(User user, Workplace workplace, Integer baseWage, Integer payday) {
        return wageRepository.saveAndFlush(Wage.builder()
                .user(user)
                .workplace(workplace)
                .baseWage(baseWage)
                .payday(payday)
                .applyStartDate(LocalDate.now())
                .applyEndDate(LocalDate.of(2999, 12, 31))
                .build());
    }

    public WorkplaceJoinHistory createWorkplaceJoinHistoryWithApprove(Workplace workplace, User user) {
        return workplaceJoinHistoryRepository.saveAndFlush(WorkplaceJoinHistory.builder()
                .workplace(workplace)
                .user(user)
                .isApprove(true)
                .decisionDate(LocalDateTime.now())
                .build());
    }

    public void createUserWorkplaceNotManager(User targetUser, Workplace workplace) {
        userWorkplaceRepository.saveAndFlush(UserWorkplace.builder()
                .user(targetUser)
                .workplace(workplace)
                .isManager(false)
                .build());
    }

    public UserWorkSchedule createUserWorkSchedule(User targetUser, WorkSchedule workSchedule) {
        return userWorkScheduleRepository.saveAndFlush(
                UserWorkSchedule.builder()
                        .user(targetUser)
                        .workSchedule(workSchedule)
                        .build()
        );
    }
}
