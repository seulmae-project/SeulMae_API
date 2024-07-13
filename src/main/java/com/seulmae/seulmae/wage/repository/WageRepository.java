package com.seulmae.seulmae.wage.repository;

import com.seulmae.seulmae.wage.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WageRepository extends JpaRepository<Wage, Long> {
}
