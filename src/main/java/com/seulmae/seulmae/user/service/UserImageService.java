package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImageService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;

    @Value("${file.storage.path.user}")
    private String userFilePath;

    @Transactional
    public void createUserImage(MultipartFile profileImage, User user) {
        try {
            String fileName = profileImage.getOriginalFilename();
            String filePath = userFilePath + user.getIdUser();
            UserImage userImage = new UserImage(user, fileName, filePath, FileUtil.getFileExtension(profileImage));
            user.updateUserImage(userImage);
            FileUtil.uploadFile(filePath, fileName, profileImage);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void updateUserImage(MultipartFile profileImage, User user) {
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String fileName = profileImage.getOriginalFilename();
                String filePath = userFilePath + user.getIdUser();
                userImageRepository.findByUser(user)
                        .ifPresentOrElse(userImage -> {

                                    FileUtil.deleteImage(userImage.getUserImagePath(), userImage.getUserImageName());

                                    userImage.update(fileName, filePath, FileUtil.getFileExtension(profileImage));
                                },
                                () -> {
                                    UserImage newUserImage = new UserImage(user, fileName, filePath, FileUtil.getFileExtension(profileImage));
                                    user.updateUserImage(newUserImage);
                                });

                FileUtil.uploadFile(filePath, fileName, profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            userImageRepository.findByUser(user)
                    .ifPresent(userImage -> {
                        FileUtil.deleteImage(userImage.getUserImagePath(), userImage.getUserImageName());
                        userImageRepository.delete(userImage);
                        user.updateUserImage(null);
                    });
        }

    }

    @Transactional
    public ResponseEntity<byte[]> getUserImage(Long userImageId) throws IOException {
        UserImage userImage = userImageRepository.findById(userImageId).orElseThrow(() -> new NullPointerException("This userImageId doesn't exist."));

        return FileUtil.getImage(userImage.getUserImagePath(), userImage.getUserImageName());
    }

}
