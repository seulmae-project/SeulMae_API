package com.seulmae.seulmae.wage.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WageRepository extends JpaRepository<Wage, Long> {

    Optional<Wage> findByUserAndWorkplace(User user, Workplace workplace);
}
