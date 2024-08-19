package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.config.oauth2.userInfo.OAuth2UserInfo;
import com.seulmae.seulmae.global.util.FileUtil;
import com.seulmae.seulmae.global.util.PasswordUtil;
import com.seulmae.seulmae.user.SocialType;
import com.seulmae.seulmae.user.dto.request.OAuthAttributesDto;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialLoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Value("${file.storage.path.user}")
    private String userFilePath;

    private static final String KAKAO = "kakao";
    private static final String APPLE = "apple";

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디가 존재하지 않습니다."));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getAccountId())
                .password(PasswordUtil.generateRandomPassword())
                .roles(user.getAuthorityRole().name())
                .build();
    }

    public User getOrCreateUser(OAuthAttributesDto attributes, SocialType socialType) {
        User findUser = userRepository.findBySocialTypeAndSocialId(socialType, attributes.getOAuth2UserInfo().getId()).orElse(null);

        if (findUser == null) {
            User savedUser = saveUser(attributes, socialType);
            saveUserImage(savedUser, attributes.getOAuth2UserInfo());
            return savedUser;
        }

        return findUser;
    }

    public SocialType getSocialType(String provider) {
        if (KAKAO.equals(provider)) {
            return SocialType.KAKAO;
        } else if (APPLE.equals(provider)) {
            return SocialType.APPLE;
        }
        return null;
    }

    private User saveUser(OAuthAttributesDto attributes, SocialType socialType) {
        User user = attributes.toEntity(socialType, attributes.getOAuth2UserInfo());
        return userRepository.save(user);
    }

    private void saveUserImage(User user, OAuth2UserInfo oAuth2UserInfo) {
        if (oAuth2UserInfo.getImageURL() != null) {
            String imageURL = oAuth2UserInfo.getImageURL();
            String imageUrlName = FileUtil.extractUrlName(imageURL);
            String imageUrlPath = userFilePath + user.getIdUser();

            UserImage userImage = new UserImage(user, imageUrlName, imageUrlPath, FileUtil.getFileExtension(imageURL));
            user.updateUserImage(userImage);
            FileUtil.downloadImageFromUrl(imageUrlPath, imageUrlName, imageURL);
            userRepository.save(user);
        }
    }


}
