package com.seulmae.seulmae.workplace.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.workplace.entity.WorkplaceImage;
import com.seulmae.seulmae.workplace.repository.WorkplaceImageRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class WorkplaceFileService {

    private final WorkplaceImageRepository workplaceImageRepository;
    @Transactional
    public ResponseEntity<byte[]> getWorkplaceImage(Long workplaceImageId) throws IOException {
        WorkplaceImage workplaceImage = workplaceImageRepository.findById(workplaceImageId).orElseThrow(() -> new NullPointerException("This workplaceImageId doesn't exist."));

        return FileUtil.getImage(workplaceImage.getWorkplaceImagePath(), workplaceImage.getWorkplaceImageName());
    }
}
