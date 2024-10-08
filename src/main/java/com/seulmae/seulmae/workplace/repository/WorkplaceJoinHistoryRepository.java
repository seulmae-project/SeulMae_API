package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.dto.WorkplaceJoinHistoryDto;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkplaceJoinHistoryRepository extends JpaRepository<WorkplaceJoinHistory, Long> {
    Optional<WorkplaceJoinHistory> findByUserAndWorkplaceAndIsApproveTrue(User user, Workplace workplace);

    @Query(value = "SELECT new com.seulmae.seulmae.workplace.dto.WorkplaceJoinHistoryDto(wjh.workplace, wjh.isApprove, wjh.decisionDate, wjh.regDateWorkplaceJoinHistory) " +
            "FROM WorkplaceJoinHistory wjh " +
            "WHERE wjh.user = :user")
    List<WorkplaceJoinHistoryDto> findAllByUser(User user);
}
