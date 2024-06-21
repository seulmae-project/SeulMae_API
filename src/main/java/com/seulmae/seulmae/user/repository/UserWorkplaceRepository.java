package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.user.entity.UserWorkplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWorkplaceRepository extends JpaRepository<UserWorkplace, Long> {
}
