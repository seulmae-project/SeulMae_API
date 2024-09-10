package com.seulmae.seulmae.global.support;

import com.seulmae.seulmae.announcement.repository.AnnouncementRepository;
import com.seulmae.seulmae.announcement.service.AnnouncementService;
import com.seulmae.seulmae.notification.repository.NotificationRepository;
import com.seulmae.seulmae.notification.service.NotificationService;
import com.seulmae.seulmae.user.repository.UserRepository;
import com.seulmae.seulmae.user.repository.UserWorkScheduleRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.user.service.UserService;
import com.seulmae.seulmae.user.service.UserWorkScheduleService;
import com.seulmae.seulmae.user.service.UserWorkplaceService;
import com.seulmae.seulmae.util.MockSetUpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // 각 테스트 이후 롤백을 통해 DB 상태를 초기화
//@ActiveProfiles("test")
public class ServiceTestSupport {
    @Autowired
    protected UserService userService;

    @Autowired
    protected UserWorkplaceService userWorkplaceService;

    @Autowired
    protected UserWorkScheduleService userWorkScheduleService;

    @Autowired
    protected NotificationService notificationService;

    @Autowired
    protected AnnouncementService announcementService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserWorkplaceRepository userWorkplaceRepository;

    @Autowired
    protected UserWorkScheduleRepository userWorkScheduleRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected AnnouncementRepository announcementRepository;

    @Autowired
    protected MockSetUpUtil mockSetUpUtil;

}
