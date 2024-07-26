package com.seulmae.seulmae.attendanceRequestHistory.repository;

import com.seulmae.seulmae.attendanceRequestHistory.entity.AttendanceRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRequestHistoryRepository extends JpaRepository<AttendanceRequestHistory, Long> {
}
