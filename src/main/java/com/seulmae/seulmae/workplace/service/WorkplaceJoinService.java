package com.seulmae.seulmae.workplace.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.notification.service.FcmTopicService;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.wage.repository.WageRepository;
import com.seulmae.seulmae.workplace.dto.JoinApprovalDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceJoinRequestDto;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkplaceJoinService {

    private final FindByIdUtil findByIdUtil;
    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceApproveRepository workplaceApproveRepository;
    private final WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;
    private final WageRepository wageRepository;
    private final UserWorkScheduleRepository userWorkScheduleRepository;

    private final WorkplaceService workplaceService;
    private final FcmTopicService fcmTopicService;

    public static final LocalDate MAX_END_DATE = LocalDate.of(2999, 12, 31);
    private final String TOPIC_PREFIX = "workplace";

    @Transactional
    public void sendJoinRequest(User user, Long workplaceId) {
        Workplace workplace = workplaceService.getWorkplaceById(workplaceId);

        WorkplaceJoinHistory workplaceJoinHistory = WorkplaceJoinHistory.builder()
                .user(user)
                .workplace(workplace)
                .isApprove(false)
                .build();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        WorkplaceApprove workplaceApprove = WorkplaceApprove.builder()
                .user(user)
                .workplace(workplace)
                .workplaceJoinHistoryId(workplaceJoinHistory.getIdWorkplaceJoinHistory())
                .build();

        workplaceApproveRepository.save(workplaceApprove);

        /** 매니저에게 알림 **/
    }

    @Transactional
    public void sendJoinApproval(Long workplaceApproveId, JoinApprovalDto request) throws FirebaseMessagingException {
        WorkplaceApprove workplaceApprove = findByIdUtil.getWorkplaceApproveById(workplaceApproveId);
        WorkSchedule workSchedule = findByIdUtil.getWorkScheduleById(request.getWorkplaceScheduleId());

        WorkplaceJoinHistory workplaceJoinHistory = findByIdUtil.getWorkplaceJoinHistoryById(workplaceApprove.getWorkplaceJoinHistoryId());
        workplaceJoinHistory.setIsApproveTrue();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        deleteWorkplaceApproveById(workplaceApproveId);

        UserWorkplace userWorkplace = UserWorkplace.builder()
                .user(workplaceJoinHistory.getUser())
                .workplace(workplaceJoinHistory.getWorkplace())
                .memo(request.getMemo())
                .isManager(false)
                .build();

        userWorkplaceRepository.save(userWorkplace);

        /** Wage **/
        Wage wage = Wage.builder()
                .user(workplaceJoinHistory.getUser())
                .workplace(workplaceJoinHistory.getWorkplace())
                .applyStartDate(LocalDate.now())
                .applyEndDate(MAX_END_DATE)
                .baseWage(request.getBaseWage())
                .payday(request.getPayday())
                .build();

        wageRepository.save(wage);

        /** UserWorkSchedule **/

        UserWorkSchedule userWorkSchedule = UserWorkSchedule.builder()
                .user(workplaceJoinHistory.getUser())
                .workSchedule(workSchedule)
                .build();

        userWorkScheduleRepository.save(userWorkSchedule);

        /** 구독 **/
        Workplace workplace = workplaceJoinHistory.getWorkplace();
        String topic = workplace.getWorkplaceTopic();

        if (topic == null) {
            topic = TOPIC_PREFIX + UUID.randomUUID().toString().replace("-","");
            workplace.setWorkplaceTopic(topic);
            workplaceRepository.save(workplace);
        }

        fcmTopicService.subscribeToTopic(workplaceJoinHistory.getUser(), topic);

        /** 알바생에세 수락 알림 **/

    }

    @Transactional
    public void sendJoinRejection(Long workplaceApproveId) {
        WorkplaceApprove workplaceApprove = findByIdUtil.getWorkplaceApproveById(workplaceApproveId);

        WorkplaceJoinHistory workplaceJoinHistory = findByIdUtil.getWorkplaceJoinHistoryById(workplaceApprove.getWorkplaceJoinHistoryId());
        workplaceJoinHistory.setIsApproveFalse();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        deleteWorkplaceApproveById(workplaceApproveId);

        /** 알바생에세 거절 알림 **/
    }

    @Transactional
    public List<WorkplaceJoinRequestDto> getWorkplaceRequestList(Long workplaceId) {
        List<WorkplaceJoinRequestDto> workplaceJoinRequestDtoList = workplaceApproveRepository.findByWorkplaceId(workplaceId);

        return workplaceJoinRequestDtoList;
    }

    public void deleteWorkplaceApproveById(Long workplaceApproveId) {
        workplaceApproveRepository.deleteById(workplaceApproveId);
    }
}
