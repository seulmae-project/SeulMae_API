package com.seulmae.seulmae.announcement.service;

import com.seulmae.seulmae.announcement.dto.request.AddAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.request.UpdateAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementDetailResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementListResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse;
import com.seulmae.seulmae.announcement.entity.Announcement;
import com.seulmae.seulmae.announcement.repository.AnnouncementRepository;
import com.seulmae.seulmae.global.dao.RedisBasicDao;
import com.seulmae.seulmae.global.util.JsonPagination;
import com.seulmae.seulmae.notification.event.AnnouncementNotificationEvent;
import com.seulmae.seulmae.notification.service.NotificationService;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.service.UserWorkplaceService;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final WorkplaceRepository workplaceRepository;

    private final RedisBasicDao redisBasicDao;

    private final UserWorkplaceService userWorkplaceService;

    private final ApplicationEventPublisher eventPublisher;

    // 공지사항 생성
    @Transactional
    public void createAnnouncement(AddAnnouncementRequest request, User user) {
        // 매니저 권한이 있는 유저여야 한다.
        Workplace workplace = workplaceRepository.findById(request.getWorkplaceId())
                .orElseThrow(() -> new NoSuchElementException("해당 근무지 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(workplace, user);

        // 생성한다.
        Announcement announcement = new Announcement(user, workplace, request.getTitle(), request.getContent(), request.getIsImportant());
        announcementRepository.save(announcement);

        // 알림을 보낸다.
        eventPublisher.publishEvent(new AnnouncementNotificationEvent(announcement));
//        notificationService.sendMessageToUsersAboutAnnouncement(announcement);
    }

    // 공지사항 수정
    @Transactional
    public void updateAnnouncement(Long announcementId, UpdateAnnouncementRequest request, User user) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoSuchElementException("해당 공지사항 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(announcement.getWorkplace(), user);

        announcement.update(request.getTitle(), request.getContent(), request.getIsImportant());
    }

    // 공지사항 상세조회
    @Transactional
    public AnnouncementDetailResponse getAnnouncement(Long announcementId, User user) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoSuchElementException("해당 공지사항 ID가 존재하지 않습니다."));
        userWorkplaceService.checkWorkplaceAuthority(announcement.getWorkplace(), user);

        String redisAnnouncementKey = announcementId.toString();
        String redisUserKey = user.getAccountId();
        int views = countViews(redisAnnouncementKey, redisUserKey);

        announcement.updateViews(views);
        announcementRepository.saveAndFlush(announcement);

        return new AnnouncementDetailResponse(announcement);
    }



    @Transactional
    public int countViews(String redisAnnouncementKey, String redisUserKey) {

        // 공지사항 키가 없을 경우, 키를 삽입하고 0으로 view를 저장한다.
        if (!redisBasicDao.hasKey(redisAnnouncementKey)) {
            redisBasicDao.setValues(redisAnnouncementKey, "0");
        }

        // 공지사항 키에 대한 값을 추출하여, view 변수에 대입한다.
        String values = redisBasicDao.getValues(redisAnnouncementKey);
        int views = Integer.parseInt(values);

        // 유저를 key로 조회한 게시글 ID 리스트안에 해당 게시글 ID가 포함되어있지 않는다면,
        if (!redisBasicDao.getValuesList(redisUserKey).contains(redisAnnouncementKey)) {
            redisBasicDao.setValuesList(redisUserKey, redisAnnouncementKey); // 유저 key로 해달 글 ID를 삽입
            views = Integer.parseInt(values) + 1; // view값을 올린다.
            redisBasicDao.setValues(redisAnnouncementKey, String.valueOf(views));  // redis에서 공지사항 키의 값을 업데이트 한다.
        }

        // 조회수를 반환한다.
        return views;
    }


    // 공지사항 리스트
    public Object getAnnouncements(Long workplaceId, User user, Integer page, Integer size) {
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무지 ID가 존재하지 않습니다."));
        userWorkplaceService.checkWorkplaceAuthority(workplace, user);

        Page<AnnouncementListResponse> announcementListResponses = announcementRepository.findAnnouncementsByWorkplace(PageRequest.of(page, size), workplace);
        return JsonPagination.buildPageResponse(announcementListResponses);
    }

    // 탑 5 공지사항
    public List<AnnouncementMainListResponse> getMainAnnouncements(Long workplaceId, User user) {
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무지 ID가 존재하지 않습니다."));
        userWorkplaceService.checkWorkplaceAuthority(workplace, user);
        List<AnnouncementMainListResponse> announcementMainListResponses = announcementRepository.findMainAnnouncementsByWorkplace(workplace);
        return announcementMainListResponses.stream().limit(5).collect(Collectors.toList());
    }


    // 대표 공지사항 리스트
    public List<AnnouncementMainListResponse> getImportantAnnouncements(Long workplaceId, User user) {
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무지 ID가 존재하지 않습니다."));
        userWorkplaceService.checkWorkplaceAuthority(workplace, user);

        List<AnnouncementMainListResponse> importantAnnouncementListResponses = announcementRepository.findImportantAnnouncementsByWorkplaceAndIsImportant(workplace, true);

        return importantAnnouncementListResponses.stream().limit(5).collect(Collectors.toList());

    }

    // 공지사항 삭제
    @Transactional
    public void deleteAnnouncement(Long announcementId, User user) {
        // 매니저 권한이 있어야 함.
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoSuchElementException("해당 공지사항 ID가 존재하지 않습니다."));
        userWorkplaceService.checkMangerAuthority(announcement.getWorkplace(), user);


        // 삭제한다.
        announcement.delete();
        announcementRepository.save(announcement);
    }
}
