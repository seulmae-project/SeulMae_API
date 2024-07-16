package com.seulmae.seulmae.attendance.repository;

import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRequestHistoryRepository extends JpaRepository<AttendanceRequestHistory, Long> {

    List<AttendanceRequestHistory> findAllByUserAndWorkplaceAndWorkDateBetween(User user, Workplace workplace, LocalDate startDate, LocalDate endDate, PageRequest of);
}
