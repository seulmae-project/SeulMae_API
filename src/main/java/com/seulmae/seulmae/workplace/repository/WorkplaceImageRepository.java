package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.workplace.entity.WorkplaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkplaceImageRepository extends JpaRepository<WorkplaceImage, Long> {
}
