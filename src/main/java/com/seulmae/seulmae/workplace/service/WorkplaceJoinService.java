package com.seulmae.seulmae.workplace.service;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkplaceJoinService {

    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceService workplaceService;
    private final WorkplaceApproveRepository workplaceApproveRepository;
    private final WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

    @Transactional
    public void sendJoinRequest(User user, Long workplaceId) {
        Workplace workplace = workplaceService.getWorkplaceById(workplaceId);

        WorkplaceApprove workplaceApprove = WorkplaceApprove.builder()
                .user(user)
                .workplace(workplace)
                .build();

        workplaceApproveRepository.save(workplaceApprove);

        WorkplaceJoinHistory workplaceJoinHistory = WorkplaceJoinHistory.builder()
                .user(user)
                .workplace(workplace)
                .isApprove(false)
                .build();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        /** 매니저에게 알림 **/
    }

    @Transactional
    public void sendJoinApproval(Long workplaceApproveId, Long workplaceJoinHistoryId) {
        deleteWorkplaceApproveById(workplaceApproveId);

        WorkplaceJoinHistory workplaceJoinHistory = getWorkplaceJoinHistoryById(workplaceJoinHistoryId);
        workplaceJoinHistory.setIsApproveTrue();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        UserWorkplace userWorkplace = UserWorkplace.builder()
                .user(workplaceJoinHistory.getUser())
                .workplace(workplaceJoinHistory.getWorkplace())
                .isManager(false)
                .build();

        userWorkplaceRepository.save(userWorkplace);

        /** 알바생에세 수락 알림 **/
    }

    @Transactional
    public void sendJoinRejection(Long workplaceApproveId, Long workplaceJoinHistoryId) {
        deleteWorkplaceApproveById(workplaceApproveId);

        WorkplaceJoinHistory workplaceJoinHistory = getWorkplaceJoinHistoryById(workplaceJoinHistoryId);
        workplaceJoinHistory.setIsApproveFalse();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        /** 알바생에세 거절 알림 **/
    }

    public WorkplaceApprove getWorkplaceApproveById(Long workplaceApproveId) {
        return workplaceApproveRepository.findById(workplaceApproveId)
                .orElseThrow(() -> new NullPointerException("This workplaceApproveId doesn't exist."));
    }

    public WorkplaceJoinHistory getWorkplaceJoinHistoryById(Long workplaceJoinHistoryId) {
        return workplaceJoinHistoryRepository.findById(workplaceJoinHistoryId)
                .orElseThrow(() -> new NullPointerException("This workplaceJoinHistoryId doesn't exist."));
    }

    /**
     * WorkplaceApprove 삭제 메소드
     **/
    public void deleteWorkplaceApproveById(Long workplaceApproveId) {
        workplaceApproveRepository.deleteById(workplaceApproveId);
    }
}
