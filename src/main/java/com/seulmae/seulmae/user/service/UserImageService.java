package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;

    @Transactional
    public UserImage createUserImage(MultipartFile profileImage, User user) {

        try {
            String fileName = profileImage.getOriginalFilename();
            String filePath = "C:\\Users\\hany\\uploads\\users\\" + user.getIdUser();
            UserImage userImage = new UserImage(user, fileName, filePath, FileUtil.getFileExtension(profileImage));
            user.updateUserImage(userImage);
            FileUtil.uploadFile(filePath, fileName, profileImage);
            userRepository.save(user);
            return userImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public ResponseEntity<byte[]> getUserImage(Long userImageId) throws IOException {
        UserImage userImage = userImageRepository.findById(userImageId).orElseThrow(() -> new NullPointerException("This userImageId doesn't exist."));

        return FileUtil.getImage(userImage.getUserImagePath(), userImage.getUserImageName());
    }

}
