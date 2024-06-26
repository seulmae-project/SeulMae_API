package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByAccountId(String accountId);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByAccountId(String accountId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
