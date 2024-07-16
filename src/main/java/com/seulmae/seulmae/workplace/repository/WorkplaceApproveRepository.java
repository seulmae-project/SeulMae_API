package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.workplace.dto.WorkplaceJoinRequestDto;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface WorkplaceApproveRepository extends JpaRepository<WorkplaceApprove, Long> {

    @Query("SELECT new com.seulmae.seulmae.workplace.dto.WorkplaceJoinRequestDto(wa.idWorkPlaceApprove, wa.user.name, wa.regDateWorkplaceApprove) " +
            "FROM WorkplaceApprove wa " +
            "WHERE wa.workplace.idWorkPlace = :workplaceId")
    List<WorkplaceJoinRequestDto> findByWorkplaceId(Long workplaceId);

    LocalDateTime findFirstRegDateWorkplaceApprove(User user, Workplace workplace);
}
