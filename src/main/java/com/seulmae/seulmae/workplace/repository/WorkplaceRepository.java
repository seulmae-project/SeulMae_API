package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {

    @Query(value = "SELECT w " +
            "FROM Workplace w " +
            "WHERE (:keyword IS NULL OR w.workplaceName LIKE %:keyword%)")
    List<Workplace> findAllByKeyword(String keyword);
}
