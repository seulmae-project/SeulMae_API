package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    Optional<UserImage> findByUser(User user);

    @Query(value = "SELECT ui " +
            "FROM UserImage ui " +
            "WHERE ui.user.idUser = :userId")
    UserImage findByUserId(Long userId);
}
