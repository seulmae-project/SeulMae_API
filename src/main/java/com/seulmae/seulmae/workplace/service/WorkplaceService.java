package com.seulmae.seulmae.workplace.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.global.util.UUIDUtil;
import com.seulmae.seulmae.global.util.UrlUtil;
import com.seulmae.seulmae.notification.service.FcmTopicServiceImpl;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceListInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceModifyDto;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceImage;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import com.seulmae.seulmae.workplace.vo.AddressVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceFileService workplaceFileService;
    private final UserWorkplaceRepository userWorkplaceRepository;

    private final FcmTopicServiceImpl fcmTopicServiceImpl;

    private final FindByIdUtil findByIdUtil;

    @Value("${file.endPoint.workplace}")
    private String fileEndPoint;

    @Transactional
    public void addWorkplace(WorkplaceAddDto workplaceAddDto, List<MultipartFile> multipartFileList, User user) {
        checkWorkplaceNameDuplicate(workplaceAddDto.getWorkplaceName());

        AddressVo addressVo = AddressVo.builder()
                .mainAddress(workplaceAddDto.getMainAddress())
                .subAddress(workplaceAddDto.getSubAddress())
                .build();

        Workplace workplace = Workplace.builder()
                .workplaceCode(UUIDUtil.generateShortUUID())
                .workplaceName(workplaceAddDto.getWorkplaceName())
                .addressVo(addressVo)
                .workplaceTel(workplaceAddDto.getWorkplaceTel())
                .build();

        /** id 생성을 위해 먼저 한 번 저장 **/
        workplaceRepository.save(workplace);

        List<WorkplaceImage> workplaceImages = workplaceFileService.addWorkplaceImage(workplace, multipartFileList);

        workplace.setWorkplaceImages(workplaceImages);
        workplaceRepository.save(workplace);

        UserWorkplace userWorkplace = UserWorkplace.builder()
                .user(user)
                .workplace(workplace)
                .isManager(true)
                .build();

        userWorkplaceRepository.save(userWorkplace);
    }

    @Transactional
    public List<WorkplaceListInfoDto> getAllWorkplace(HttpServletRequest request, String keyword) {
        /** 검색 가능하도록 수정 필요 **/
        List<Workplace> workplaceList = workplaceRepository.findAllByKeyword(keyword);
        List<WorkplaceListInfoDto> workplaceListInfoDtoList = new ArrayList<>();

        workplaceList.stream()
                .forEach(workplace -> {
                    String workplaceManagerName = null; /** 추후 추가 **/
                    String WorkplaceThumbnailUrl = getWorkplaceThumbnailUrl(workplace, request);
                    WorkplaceListInfoDto workplaceListInfoDto = new WorkplaceListInfoDto(workplace, null, workplaceManagerName, WorkplaceThumbnailUrl);

                    workplaceListInfoDtoList.add(workplaceListInfoDto);
                });

        return workplaceListInfoDtoList;
    }

    @Transactional
    public WorkplaceInfoDto getSpecificWorkplace(Long workplaceId, HttpServletRequest request) {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);

        List<String> workplaceImageUrlList = getWorkplaceImageUrlList(workplace, request);

        return new WorkplaceInfoDto(workplace, workplaceImageUrlList);
    }

    @Transactional
    public void modifyWorkplace(WorkplaceModifyDto workplaceModifyDto, List<MultipartFile> multipartFileList) throws IOException {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceModifyDto.getWorkplaceId());

        AddressVo addressVo = AddressVo.builder()
                .mainAddress(workplaceModifyDto.getMainAddress())
                .subAddress(workplaceModifyDto.getSubAddress())
                .build();

        Workplace updatedWorkplace = Workplace.builder()
                .idWorkPlace(workplace.getIdWorkPlace())
                .workplaceName(workplaceModifyDto.getWorkplaceName())
                .addressVo(addressVo)
                .workplaceTel(workplaceModifyDto.getWorkplaceTel())
                .regDateWorkplace(workplace.getRegDateWorkplace())
                .build();

        List<WorkplaceImage> workplaceImageList = workplaceFileService.modifyWorkplaceImage(updatedWorkplace, multipartFileList);

        updatedWorkplace.setWorkplaceImages(workplaceImageList);
        workplaceRepository.save(updatedWorkplace);
    }

    @Transactional
    public void deleteWorkplace(Long workplaceId) throws FirebaseMessagingException {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);

        workplace.deleteWorkplace();

        workplaceRepository.save(workplace);

        /**
         * 구독 전체 취소
         */
        List<User> users = userWorkplaceRepository.findUsersByWorkplace(workplace);
        fcmTopicServiceImpl.unsubscribeAllFromTopic(users, workplace.getWorkplaceTopic());
    }

    public String getWorkplaceThumbnailUrl(Workplace workplace, HttpServletRequest request) {

        Long workplaceImageId = workplace.getWorkplaceImages() != null ?
                workplace.getWorkplaceImages().stream()
                        .sorted(Comparator.comparingInt(WorkplaceImage::getSequence))
                        .findFirst()
                        .map(WorkplaceImage::getIdWorkPlaceImage)
                        .orElse(null)
                : null;

        String workplaceImageUrl = workplaceImageId != null ? UrlUtil.getBaseUrl(request) + fileEndPoint + "?workplaceImageId=" + workplaceImageId : null;

        return workplaceImageUrl;
    }

    public List<String> getWorkplaceImageUrlList(Workplace workplace, HttpServletRequest request) {
        List<String> workplaceImageUrlList = new ArrayList<>();

        workplace.getWorkplaceImages().stream()
                .sorted(Comparator.comparingInt(WorkplaceImage::getSequence))
                .forEach(workplaceImage -> {
                    Long workplaceImageId = workplaceImage.getIdWorkPlaceImage();

                    String workplaceImageUrl = workplaceImageId != null ? UrlUtil.getBaseUrl(request) + fileEndPoint + "?workplaceImageId=" + workplaceImageId : null;
                    workplaceImageUrlList.add(workplaceImageUrl);
                });

        return workplaceImageUrlList;
    }

    @Transactional
    public void checkWorkplaceNameDuplicate(String workplaceName) {
        if (existsByWorkplaceName(workplaceName)) {
            throw new DuplicateKeyException("이미 등록된 근무지명입니다.");
        }
    }
    public boolean existsByWorkplaceName(String workplaceName) {
        return workplaceRepository.existsByWorkplaceName(workplaceName);
    }
}
