package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.user.service.UserImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserImageController {
    private final UserImageService userImageService;

    @GetMapping("/file")
    public ResponseEntity<?> getUserImage(@RequestParam Long userImageId) throws IOException {
        try {
            return userImageService.getUserImage(userImageId);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }
}
