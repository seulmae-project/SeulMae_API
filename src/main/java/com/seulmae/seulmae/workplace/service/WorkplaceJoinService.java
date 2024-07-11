package com.seulmae.seulmae.workplace.service;

import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.dto.WorkplaceJoinRequestDto;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkplaceJoinService {

    private final FindByIdUtil findByIdUtil;
    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceService workplaceService;
    private final WorkplaceApproveRepository workplaceApproveRepository;
    private final WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;

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
    public void sendJoinApproval(Long workplaceApproveId) {
        WorkplaceApprove workplaceApprove = findByIdUtil.getWorkplaceApproveById(workplaceApproveId);

        WorkplaceJoinHistory workplaceJoinHistory = findByIdUtil.getWorkplaceJoinHistoryById(workplaceApprove.getWorkplaceJoinHistoryId());
        workplaceJoinHistory.setIsApproveTrue();

        workplaceJoinHistoryRepository.save(workplaceJoinHistory);

        deleteWorkplaceApproveById(workplaceApproveId);

        UserWorkplace userWorkplace = UserWorkplace.builder()
                .user(workplaceJoinHistory.getUser())
                .workplace(workplaceJoinHistory.getWorkplace())
                .isManager(false)
                .build();

        userWorkplaceRepository.save(userWorkplace);

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
