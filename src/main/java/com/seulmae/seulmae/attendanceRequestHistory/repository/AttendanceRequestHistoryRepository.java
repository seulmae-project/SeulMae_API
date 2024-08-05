package com.seulmae.seulmae.attendanceRequestHistory.repository;

import com.seulmae.seulmae.attendance.dto.AttendanceManagerMainListDto;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.attendanceRequestHistory.entity.AttendanceRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Query(value = "SELECT new com.seulmae.seulmae.attendance.dto.AttendanceManagerMainListDto(arh.idAttendanceRequestHistory, a.user.name, a.user.idUser, arh.workStartTime, arh.workEndTime, arh.totalWorkTime, arh.isRequestApprove, arh.isManagerCheck) " +
            "FROM AttendanceRequestHistory arh " +
            "JOIN Attendance a on a = arh.attendance " +
            "WHERE a.workplace = :workplace " +
            "AND a.workDate = :localDate")
    List<AttendanceManagerMainListDto> findByWorkplaceAndDate(Workplace workplace, LocalDate localDate);
}
