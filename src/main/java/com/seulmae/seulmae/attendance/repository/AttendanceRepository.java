package com.seulmae.seulmae.attendance.repository;

import com.seulmae.seulmae.attendance.entity.Attendance;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserAndWorkplaceAndWorkDate(User user, Workplace workplace, LocalDateTime workDate);
}
