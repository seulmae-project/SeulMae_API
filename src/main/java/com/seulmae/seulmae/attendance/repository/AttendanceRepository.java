package com.seulmae.seulmae.attendance.repository;

import com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto;
import com.seulmae.seulmae.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT new com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto(arh.idAttendanceRequestHistory, a.user.name, arh.regDateAttendanceRequestHistory) " +
            "FROM Attendance a " +
            "JOIN AttendanceRequestHistory arh on a.idAttendance = arh.attendance.idAttendance " +
            "WHERE a.workplace.idWorkPlace = :workplaceId " +
            "AND arh.isManagerCheck = false " +
            "AND arh.isRequestApprove = false " +
            "ORDER BY arh.regDateAttendanceRequestHistory")
    List<AttendanceRequestListDto> findByWorkplaceId(Long workplaceId);
}
