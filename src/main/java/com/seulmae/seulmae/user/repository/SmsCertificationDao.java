package com.seulmae.seulmae.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsCertificationDao {
    private final String PREFIX_SMS = "sms: ";
    private final int LIMIT_TIME = 3 * 60;

    private final StringRedisTemplate redisTemplate;


    public void createSmsCertification(String phoneNumber, String authCode) {
        redisTemplate.opsForValue()
                .set(PREFIX_SMS + phoneNumber, authCode, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getSmsCertification(String phoneNumber) {
        return redisTemplate.opsForValue().get(PREFIX_SMS + phoneNumber);
    }

    public void removeSmsCertification(String phoneNumber) {
        redisTemplate.delete(PREFIX_SMS + phoneNumber);
    }

    public boolean hasKey(String phoneNumber) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX_SMS + phoneNumber));
    }
}
