package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkScheduleDayRepository extends JpaRepository<WorkScheduleDay, Long> {
    List<WorkScheduleDay> findAllByWorkSchedule(WorkSchedule workSchedule);
}
