package com.seulmae.seulmae.attendanceRequestHistory.repository;

import com.seulmae.seulmae.attendance.dto.AttendanceManagerMainListDto;
import com.seulmae.seulmae.attendanceRequestHistory.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query(value = "SELECT new com.seulmae.seulmae.attendance.dto.AttendanceManagerMainListDto(a.user.name, arh.workStartTime, arh.workEndTime, arh.totalWorkTime, arh.isRequestApprove, arh.isManagerCheck) " +
            "FROM AttendanceRequestHistory arh " +
            "JOIN Attendance a on a = arh.attendance " +
            "WHERE a.workplace = :workplace " +
            "AND a.workDate = :localDate")
    List<AttendanceManagerMainListDto> findByWorkplaceAndDate(Workplace workplace, LocalDate localDate);

    @Query("SELECT COUNT(arh) " +
            "FROM AttendanceRequestHistory arh " +
            "WHERE arh.attendance.workplace.idWorkPlace = :workplaceId")
    long countByWorkplaceId(Long workplaceId);

    @Query("SELECT arh FROM AttendanceRequestHistory arh " +
            "WHERE arh.attendance.workplace.idWorkPlace = :workplaceId " +
            "AND YEAR(arh.attendance.workDate) = :year " +
            "AND MONTH(arh.attendance.workDate) = :month")
    Page<AttendanceRequestHistory> findByWorkplaceIdAndYearAndMonth(Long workplaceId, Integer year, Integer month, Pageable pageable);

    @Query("SELECT SUM(arh.totalWorkTime) " +
            "FROM AttendanceRequestHistory arh " +
            "WHERE arh.attendance.workplace.idWorkPlace = :workplaceId " +
            "AND arh.attendance.user.idUser = :userId " +
            "AND YEAR(arh.attendance.workDate) = :year " +
            "AND MONTH(arh.attendance.workDate) = :month " +
            "AND arh.isRequestApprove = true")
    BigDecimal sumMonthlyWorkTime(Long userId, Long workplaceId, Integer year, Integer month);
}
