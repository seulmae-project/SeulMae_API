package com.seulmae.seulmae.workplace.repository;

import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkplaceImageRepository extends JpaRepository<WorkplaceImage, Long> {

    List<WorkplaceImage> findByWorkplace(Workplace workplace);

    void deleteAllByWorkplace(Workplace workplace);

}
