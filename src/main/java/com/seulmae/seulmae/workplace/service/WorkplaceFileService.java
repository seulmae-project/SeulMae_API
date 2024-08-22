package com.seulmae.seulmae.workplace.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceImage;
import com.seulmae.seulmae.workplace.repository.WorkplaceImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkplaceFileService {

    private final WorkplaceImageRepository workplaceImageRepository;

    @Value("${file.storage.path.workplace}")
    private String workplaceFilePath;

    @Transactional
    public ResponseEntity<byte[]> getWorkplaceImage(Long workplaceImageId) throws IOException {
        WorkplaceImage workplaceImage = workplaceImageRepository.findById(workplaceImageId).orElseThrow(() -> new NullPointerException("This workplaceImageId doesn't exist."));

        return FileUtil.getImage(workplaceImage.getWorkplaceImagePath(), workplaceImage.getWorkplaceImageName());
    }

    @Transactional
    public List<WorkplaceImage> modifyWorkplaceImage(Workplace workplace, List<MultipartFile> multipartFileList) throws IOException {
        List<WorkplaceImage> workplaceImageList = workplaceImageRepository.findByWorkplace(workplace);

        if (workplaceImageList != null) {
            for (WorkplaceImage workplaceImage : workplaceImageList) {
                FileUtil.deleteImage(workplaceImage.getWorkplaceImagePath(), workplaceImage.getWorkplaceImageName());
            }

            workplaceImageRepository.deleteAllByWorkplace(workplace);
        }
        return addWorkplaceImage(workplace, multipartFileList);
    }

    public List<WorkplaceImage> addWorkplaceImage(Workplace workplace, List<MultipartFile> multipartFileList) {
        int i = 1;
        List<WorkplaceImage> workplaceImages = new ArrayList<>();

        if (multipartFileList != null && !multipartFileList.isEmpty()) {
            for (MultipartFile multipartFile : multipartFileList) {
                if (multipartFile != null && !multipartFile.isEmpty()) {
                    try {
                        String fileName = multipartFile.getOriginalFilename();
                        String filePath = workplaceFilePath + workplace.getIdWorkPlace();

                        WorkplaceImage workplaceImage = WorkplaceImage.builder()
                                .workplace(workplace)
                                .workplaceImageName(fileName)
                                .workplaceImagePath(filePath)
                                .workplaceImageExtension(FileUtil.getFileExtension(multipartFile))
                                .sequence(i)
                                .build();

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
        return workplaceImages;
    }
}

