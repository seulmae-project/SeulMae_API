package com.seulmae.seulmae.notification.repository;

import com.seulmae.seulmae.notification.entity.FcmToken;
import com.seulmae.seulmae.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<User> findUserByFcmToken(String fcmToken);
}
