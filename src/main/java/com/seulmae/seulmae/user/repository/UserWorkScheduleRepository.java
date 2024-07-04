package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWorkScheduleRepository extends JpaRepository<UserWorkSchedule, Long> {


}
