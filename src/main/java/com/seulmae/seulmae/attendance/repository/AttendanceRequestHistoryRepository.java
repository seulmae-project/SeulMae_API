package com.seulmae.seulmae.attendance.repository;

import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface AttendanceRequestHistoryRepository extends JpaRepository<AttendanceRequestHistory, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(arh) > 0 THEN true ELSE false END " +
            "FROM AttendanceRequestHistory arh " +
            "JOIN Attendance a on a = arh.attendance " +
            "WHERE a.workDate = :workDate " +
            "AND a.user = :user " +
            "AND (:dtoWorkStartTime BETWEEN arh.workStartTime AND arh.workEndTime OR :dtoWorkEndTime BETWEEN arh.workStartTime AND arh.workEndTime) " +
            "AND (arh.isRequestApprove = true OR (arh.isRequestApprove = false AND arh.isManagerCheck = false))")
    Boolean existByUserAndWorkDate(User user, LocalDate workDate, LocalDateTime dtoWorkStartTime, LocalDateTime dtoWorkEndTime);
}
