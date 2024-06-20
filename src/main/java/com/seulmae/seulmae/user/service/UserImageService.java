package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final UserRepository userRepository;

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

}
