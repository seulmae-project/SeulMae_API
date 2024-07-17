package com.seulmae.seulmae.attendance.repository;

import com.seulmae.seulmae.attendance.dto.AttendanceRequestDto;
import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRequestHistoryRepository extends JpaRepository<AttendanceRequestHistory, Long> {

    @Query(value = "SELECT arh " +
            "FROM AttendanceRequestHistory arh " +
            "JOIN Attendance a on a = arh.attendance " +
            "WHERE a.workDate = :workDate " +
            "AND a.user = :user " +
            "AND (:dtoWorkStartTime BETWEEN arh.workStartTime AND arh.workEndTime OR :dtoWorkEndTime BETWEEN arh.workStartTime AND arh.workEndTime) " +
            "AND arh.isRequestApprove = true OR (arh.isRequestApprove = false AND arh.isManagerCheck = false)")
    Boolean existByUserAndWorkDate(User user, LocalDate workDate, LocalDateTime dtoWorkStartTime, LocalDateTime dtoWorkEndTime);
}
