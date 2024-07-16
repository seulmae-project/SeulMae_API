package com.seulmae.seulmae.attendance.repository;

import com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto;
import com.seulmae.seulmae.attendance.entity.Attendance;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT new com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto(arh.idAttendanceRequestHistory, a.user.name, arh.regDateAttendance) " +
            "FROM Attendance a " +
            "JOIN AttendanceRequestHistory arh on a.idAttendance = arh.attendance.idAttendance " +
            "WHERE a.workplace.idWorkPlace = :workplaceId " +
            "AND arh.isManagerCheck = false " +
            "AND arh.isRequestApprove = false " +
            "ORDER BY arh.regDateAttendance")
    List<AttendanceRequestListDto> findByWorkplaceId(Long workplaceId);

    Optional<Attendance> findByUserAndWorkplaceAndWorkDate(User user, Workplace workplace, LocalDateTime workDate);
}
