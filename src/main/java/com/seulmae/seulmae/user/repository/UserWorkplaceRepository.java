package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWorkplaceRepository extends JpaRepository<UserWorkplace, Long> {
    Optional<User> findUserByWorkplaceAndIsManager(Workplace workplace, boolean isManager);

    boolean existsByWorkplaceAndUserAndIsManager(Workplace workplace, User user, boolean isManager);

    boolean existsByWorkplaceAndUser(Workplace workplace, User user);
}
