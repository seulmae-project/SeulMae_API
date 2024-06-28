package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkplaceJoinHistoryRepository extends JpaRepository<WorkplaceJoinHistory, Long> {
}
