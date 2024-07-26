package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkplaceJoinHistoryRepository extends JpaRepository<WorkplaceJoinHistory, Long> {
    Optional<WorkplaceJoinHistory> findByUserAndWorkplace(User user, Workplace workplace);
}
