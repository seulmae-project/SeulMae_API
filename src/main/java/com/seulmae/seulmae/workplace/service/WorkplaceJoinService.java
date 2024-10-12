package com.seulmae.seulmae.workplace.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.seulmae.seulmae.global.util.DateFormatUtil;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.event.MultiDeviceNotificationEvent;
import com.seulmae.seulmae.notification.service.FcmTopicServiceImpl;
import com.seulmae.seulmae.notification.service.NotificationService;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.wage.repository.WageRepository;
import com.seulmae.seulmae.workplace.dto.*;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final NotificationService notificationService;
    private final FcmTopicServiceImpl fcmTopicServiceImpl;

    private final ApplicationEventPublisher eventPublisher;

    public static final LocalDate MAX_END_DATE = LocalDate.of(2999, 12, 31);
    private static final String TOPIC_PREFIX = "workplace";

    @Transactional
    public void sendJoinRequest(User user, WorkplaceJoinDto workplaceJoinDto) {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceJoinDto.getWorkplaceId());
        User requestUser = findByIdUtil.getUserById(user.getIdUser());
        User manager = userWorkplaceRepository.findUserByWorkplaceAndIsManager(workplace, true)
                .orElseThrow(() -> new NoSuchElementException("해당 근무지의 매니저가 존재하지 않습니다."));

        /** 중복 가입 신청 확인 **/
        checkDuplicateWorkplaceJoin(requestUser, workplace);

        WorkplaceJoinHistory workplaceJoinHistory = WorkplaceJoinHistory.builder()
                .user(requestUser)
                .workplace(workplace)
                .isApprove(false)
                .build();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        WorkplaceApprove workplaceApprove = WorkplaceApprove.builder()
                .user(requestUser)
                .workplace(workplace)
                .workplaceJoinHistoryId(workplaceJoinHistory.getIdWorkplaceJoinHistory())
                .build();

        workplaceApproveRepository.save(workplaceApprove);

        /** 매니저에게 알림 **/
        // 이벤트 발행
        String title = "[가입요청]";
        String body = "'" + requestUser.getName() + "'이 가입요청 하였습니다.";
        eventPublisher.publishEvent(new MultiDeviceNotificationEvent(title, body, manager, NotificationType.JOIN_REQUEST, workplaceApprove.getIdWorkPlaceApprove(), workplaceJoinDto.getWorkplaceId()));
//        notificationService.sendMessageToUserWithMultiDevice(title, body, manager, NotificationType.JOIN_REQUEST, workplaceApprove.getIdWorkPlaceApprove(), workplaceJoinDto.getWorkplaceId());
    }

    @Transactional
    public void sendJoinApproval(Long workplaceApproveId, JoinApprovalDto request) throws FirebaseMessagingException {
        WorkplaceApprove workplaceApprove = findByIdUtil.getWorkplaceApproveById(workplaceApproveId);
        WorkSchedule workSchedule = null;
        if(request.getWorkplaceScheduleId() != null) {
            workSchedule = findByIdUtil.getWorkScheduleById(request.getWorkplaceScheduleId());
        }

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

        if (workSchedule != null) {
            UserWorkSchedule userWorkSchedule = UserWorkSchedule.builder()
                    .user(workplaceJoinHistory.getUser())
                    .workSchedule(workSchedule)
                    .build();

            userWorkScheduleRepository.save(userWorkSchedule);
        }

        /** 구독 **/
        Workplace workplace = workplaceJoinHistory.getWorkplace();
        String topic = workplace.getWorkplaceTopic();

        if (topic == null) {
            topic = TOPIC_PREFIX + UUID.randomUUID().toString().replace("-","");
            workplace.setWorkplaceTopic(topic);
            workplaceRepository.save(workplace);
        }

        fcmTopicServiceImpl.subscribeToTopic(workplaceJoinHistory.getUser(), topic);

        /** 알바생에게 수락 알림 **/
        String title = "[가입수락]";
        String body = "'" + workplace.getWorkplaceName() + "'에 가입되었습니다.";
        eventPublisher.publishEvent(new MultiDeviceNotificationEvent(title, body, workplaceJoinHistory.getUser(), NotificationType.JOIN_RESPONSE, workplaceJoinHistory.getIdWorkplaceJoinHistory(), workplaceJoinHistory.getWorkplace().getIdWorkPlace()));
//        notificationService.sendMessageToUserWithMultiDevice(title, body, workplaceJoinHistory.getUser(), NotificationType.JOIN_RESPONSE, workplaceJoinHistory.getIdWorkplaceJoinHistory(), workplaceJoinHistory.getWorkplace().getIdWorkPlace());
    }

    @Transactional
    public void sendJoinRejection(Long workplaceApproveId) {
        WorkplaceApprove workplaceApprove = findByIdUtil.getWorkplaceApproveById(workplaceApproveId);

        WorkplaceJoinHistory workplaceJoinHistory = findByIdUtil.getWorkplaceJoinHistoryById(workplaceApprove.getWorkplaceJoinHistoryId());
        workplaceJoinHistory.setIsApproveFalse();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        deleteWorkplaceApproveById(workplaceApproveId);

        /** 알바생에세 거절 알림 **/
        String title = "[가입거절]";
        String body = "'" + workplaceJoinHistory.getWorkplace().getWorkplaceName() + "'에 가입이 거절됐습니다. 해당 근무지의 매니저에게 문의하세요.";
        eventPublisher.publishEvent(new MultiDeviceNotificationEvent(title, body, workplaceJoinHistory.getUser(), NotificationType.JOIN_RESPONSE, workplaceJoinHistory.getIdWorkplaceJoinHistory(), workplaceJoinHistory.getWorkplace().getIdWorkPlace()));
//        notificationService.sendMessageToUserWithMultiDevice(title, body, workplaceJoinHistory.getUser(), NotificationType.JOIN_RESPONSE, workplaceJoinHistory.getIdWorkplaceJoinHistory(), workplaceJoinHistory.getWorkplace().getIdWorkPlace());
    }

    @Transactional
    public List<WorkplaceJoinRequestDto> getWorkplaceRequestList(Long workplaceId) {
        List<WorkplaceJoinRequestDto> workplaceJoinRequestDtoList = workplaceApproveRepository.findByWorkplaceId(workplaceId);

        return workplaceJoinRequestDtoList;
    }

    @Transactional
    public List<WorkplaceJoinHistoryResponse> getJoinRequestList(User user) {
        List<WorkplaceJoinHistoryDto> workplaceJoinHistoryList = workplaceJoinHistoryRepository.findAllByUser(user);

        List<WorkplaceJoinHistoryResponse> workplaceJoinHistoryResponseList = workplaceJoinHistoryList.stream()
                .map(workplaceJoinHistoryDto -> WorkplaceJoinHistoryResponse.builder()
                        .workplaceId(workplaceJoinHistoryDto.getWorkplace().getIdWorkPlace())
                        .workplaceName(workplaceJoinHistoryDto.getWorkplace().getWorkplaceName())
                        .isApprove(workplaceJoinHistoryDto.getIsApprove())
                        .decisionDate(DateFormatUtil.formatToDateTimeString(workplaceJoinHistoryDto.getDecisionDate()))
                        .regDateWorkplaceJoinHistory(DateFormatUtil.formatToDateTimeString(workplaceJoinHistoryDto.getRegDateWorkplaceJoinHistory()))
                        .build()).toList();

        return workplaceJoinHistoryResponseList;
    }

    public void deleteWorkplaceApproveById(Long workplaceApproveId) {
        workplaceApproveRepository.deleteById(workplaceApproveId);
    }

    public void checkDuplicateWorkplaceJoin(User user, Workplace workplace) {
        if (existsByUserAndWorkplace(user, workplace)) {
            throw new DuplicateKeyException("이미 해당 근무지에 가입 신청이 되어있습니다. 확인 후 다시 시도해 주세요.");
        }
    }
    public boolean existsByUserAndWorkplace(User user, Workplace workplace) {
        return workplaceApproveRepository.existsByUserAndWorkplace(user, workplace);
    }
}
