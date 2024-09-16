package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.global.support.RepositoryUnitTestSupport;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryUnitTest extends RepositoryUnitTestSupport {

    @Autowired
    private UserRepository userRepository;

    final String accountId = "test1234";
    final String password = "qwer1234!";
    final String phoneNumber = "010123412b34";
    final String name = "테스트이름";
    final String birthday = "19930815";
    final boolean isMale = false;

    @Test
    void existsByAccountId() {
        User user1 = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        boolean result = userRepository.existsByAccountId(user1.getAccountId());

        assertThat(result).isTrue();
    }

    @Test
    void existsByPhoneNumber() {
        User user1 = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        boolean result = userRepository.existsByPhoneNumber(user1.getPhoneNumber());

        assertThat(result).isTrue();
    }

    @Test
    void existsByAccountIdAndPhoneNumber() {
        User user1 = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        boolean result = userRepository.existsByAccountIdAndPhoneNumber(user1.getAccountId(), user1.getPhoneNumber());

        assertThat(result).isTrue();
    }

    @Test
    void findByAccountId() {
        User user1 = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        User resultUser = userRepository.findByAccountId(user1.getAccountId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(resultUser)
                .extracting("name", "birthday")
                .containsExactly(name, birthday);
    }

    @Test
    void findByPhoneNumber() {
        User user1 = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        User resultUser = userRepository.findByPhoneNumber(user1.getPhoneNumber())
                .orElseThrow(NoSuchElementException::new);

        assertThat(resultUser)
                .extracting("name", "birthday")
                .containsExactly(name, birthday);
    }

    @Test
    void findByRefreshToken() {
        User user1 = mockSetUpUtil.createUser(accountId, password, phoneNumber, name, birthday, isMale);
        user1.updateRefreshToken("refreshToken");

        User resultUser = userRepository.findByRefreshToken(user1.getRefreshToken())
                .orElseThrow(NoSuchElementException::new);
        assertThat(resultUser)
                .extracting("name", "birthday")
                .containsExactly(name, birthday);
    }

    @Test
    void findBySocialTypeAndSocialId() {
        User socialUser = mockSetUpUtil.creatSocialUser(accountId, password, "socialId", SocialType.KAKAO);
        User resultUser = userRepository.findBySocialTypeAndSocialId(socialUser.getSocialType(), socialUser.getSocialId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(resultUser)
                .extracting("accountId")
                .isEqualTo(accountId);

    }
}