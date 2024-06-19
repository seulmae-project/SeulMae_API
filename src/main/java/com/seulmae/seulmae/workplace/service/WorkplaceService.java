package com.seulmae.seulmae.workplace.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.global.util.UrlUtil;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;
    private String fileEndPoint = "/api/workplace/v1/file";

    @Transactional
    public void addWorkplace(WorkplaceAddDto workplaceAddDto, List<MultipartFile> multipartFileList) {
        Workplace workplace = new Workplace(workplaceAddDto);

        /** id 생성을 위해 먼저 한 번 저장 **/
        workplaceRepository.save(workplace);

        int i = 1;
        List<WorkplaceImage> workplaceImages = new ArrayList<>();

        if (multipartFileList != null && !multipartFileList.isEmpty()) {

            for (MultipartFile multipartFile : multipartFileList) {

                if (multipartFile != null && !multipartFile.isEmpty()) {
                    try {
                        String fileName = multipartFile.getOriginalFilename();
//                String filePath = "/app/workplace" + workplace.getIdWorkPlace();
                        String filePath = "C:\\workplace\\" + workplace.getIdWorkPlace();
                        WorkplaceImage workplaceImage = new WorkplaceImage(workplace, fileName, filePath, FileUtil.getFileExtension(multipartFile), i);
                        workplaceImage.setWorkplace(workplace);

                        workplaceImages.add(workplaceImage);

                        FileUtil.uploadFile(filePath, fileName, multipartFile);
                        i++;
                    } catch (Exception e) {
                        // 예외 처리
                        e.printStackTrace();
                    }
                }
            }
        }

        workplace.setWorkplaceImages(workplaceImages);
        workplaceRepository.save(workplace);
    }

    @Transactional
    public List<WorkplaceListInfoDto> getAllWorkplace(HttpServletRequest request) {
        List<Workplace> workplaceList = workplaceRepository.findAll();
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
        Workplace workplace = workplaceRepository.findById(workplaceId).orElseThrow(() -> new NullPointerException("This workplaceId doesn't exist."));

        List<String> workplaceImageUrlList = getWorkplaceImageUrlList(workplace, request);

        return new WorkplaceInfoDto(workplace, workplaceImageUrlList);
    }

    @Transactional
    public void modifyWorkplace(WorkplaceModifyDto workplaceModifyDto, List<MultipartFile> multipartFileList) {
        Workplace workplace = workplaceRepository.findById(workplaceModifyDto.getWorkplaceId()).orElseThrow(() -> new NullPointerException("This workplaceId doesn't exist."));

        workplace.builder()
                .idWorkPlace(workplace.getIdWorkPlace())
                .workplaceCode(workplace.getWorkplaceCode())
                .workplaceName(workplaceModifyDto.getWorkplaceName())
                .addressVo(new AddressVo(workplaceModifyDto.getMainAddress(), workplaceModifyDto.getSubAddress()))
                .workplaceTel(workplaceModifyDto.getWorkplaceTel())
                .build();

        workplaceRepository.save(workplace);
    }

    @Transactional
    public void deleteWorkplace(Long workplaceId) {
        Workplace workplace = workplaceRepository.findById(workplaceId).orElseThrow(() -> new NullPointerException("This workplaceId doesn't exist."));

        workplace.deleteWorkplace();

        workplaceRepository.save(workplace);
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
}
