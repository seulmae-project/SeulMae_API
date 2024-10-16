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

    @Query("SELECT uw.user FROM UserWorkplace uw " +
            "WHERE uw.workplace = :workplace " +
            "AND uw.isManager = :isManager " +
            "AND uw.isDelUserWorkplace is false")
    Optional<User> findUserByWorkplaceAndIsManager(Workplace workplace, boolean isManager);

    Optional<UserWorkplace> findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(User user, Workplace workplace);

    @Query("SELECT uw.workplace FROM UserWorkplace uw " +
            "WHERE uw.user = :user " +
            "AND uw.isDelUserWorkplace is false")
    List<Workplace> findWorkplacesByUser(User user);

    List<UserWorkplace> findAllByUserAndIsDelUserWorkplaceFalse(User user);

    @Query("SELECT uw.user FROM UserWorkplace uw " +
            "WHERE uw.workplace = :workplace " +
            "AND uw.isDelUserWorkplace is false")
    List<User> findUsersByWorkplace(Workplace workplace);

    boolean existsByWorkplaceAndUserAndIsManagerAndIsDelUserWorkplaceFalse(Workplace workplace, User user, boolean isManager);

    boolean existsByWorkplaceAndUserAndIsDelUserWorkplaceFalse(Workplace workplace, User user);

    @Query(value = "SELECT uw " +
            "FROM UserWorkplace uw " +
            "WHERE uw.workplace.idWorkPlace = :workplaceId")
    List<UserWorkplace> findByWorkplaceId(Long workplaceId);
}
