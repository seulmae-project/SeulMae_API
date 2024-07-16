package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WorkplaceApproveRepository extends JpaRepository<WorkplaceApprove, Long> {
    LocalDateTime findFirstRegDateWorkplaceApprove(User user, Workplace workplace);
}
