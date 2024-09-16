package com.seulmae.seulmae.user.repository;

import com.seulmae.seulmae.global.support.RepositoryUnitTestSupport;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.workplace.entity.Workplace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserWorkplaceRepositoryUnitTest extends RepositoryUnitTestSupport {
    @Autowired
    private UserWorkplaceRepository userWorkplaceRepository;

    private User initUser;
    private Workplace initWorkplace;
    private UserWorkplace initUserWorkplace;

    @BeforeEach
    void setUp() {
        initUser = mockSetUpUtil.createUser("test1234", "pw", "phoneNumber", "name", "19920221", true);
        initWorkplace = mockSetUpUtil.createWorkplace("workplace", "main", "sub", "0311231234");
        initUserWorkplace = mockSetUpUtil.createUserWorkplace(initUser, initWorkplace, true);
    }

    @Test
    void findByUserAndWorkplaceAndIsDelUserWorkplaceFalse() {
        UserWorkplace targetUserWorkplace = userWorkplaceRepository.findByUserAndWorkplaceAndIsDelUserWorkplaceFalse(initUser, initWorkplace)
                .orElse(null);
        assertThat(targetUserWorkplace).isNotNull();
        assertThat(targetUserWorkplace.getUser()).isEqualTo(initUser);
        assertThat(targetUserWorkplace.getWorkplace()).isEqualTo(initWorkplace);

    }
}