package com.seulmae.seulmae.attendance.repository;

import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRequestHistoryRepository extends JpaRepository<AttendanceRequestHistory, Long> {
}
