package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserWorkScheduleRepository extends JpaRepository<UserWorkSchedule, Long> {

    List<UserWorkSchedule> findAllByWorkSchedule(WorkSchedule workSchedule);

    @Query("SELECT uws FROM UserWorkSchedule uws " +
            "WHERE uws.user = :user " +
            "AND uws.workSchedule.workplace = :workplace ")
    Optional<UserWorkSchedule> findByUserAndWorkplace(User user, Workplace workplace);
}
