package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkplaceApproveRepository extends JpaRepository<WorkplaceApprove, Long> {
}
