package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findAllByWorkplace(Workplace workplace);

    @Query(value = "SELECT ws " +
            "FROM WorkSchedule ws " +
            "JOIN UserWorkSchedule uws ON uws.workSchedule = ws " +
            "JOIN WorkScheduleDay wsd ON wsd.workSchedule = ws " +
            "WHERE uws.user = :user " +
            "AND ws.workplace = :workplace " +
            "AND wsd.day = :day")
    List<WorkSchedule> findWorkSchedulesByUserAndWorkplace(
            @Param("user") User user,
            @Param("workplace") Workplace workplace,
            @Param("day") Day day);
}
