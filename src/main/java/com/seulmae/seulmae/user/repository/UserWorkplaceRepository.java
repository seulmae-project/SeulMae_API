package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWorkplaceRepository extends JpaRepository<UserWorkplace, Long> {
    Optional<User> findUserByWorkplaceAndIsManager(Workplace workplace, boolean isManager);

    @Query("SELECT uw.workplace FROM UserWorkplace uw " +
            "WHERE uw.user = :user")
    List<Workplace> findWorkplacesByUser(User user);
}
