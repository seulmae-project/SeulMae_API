package com.seulmae.seulmae.global.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/version")
public class VersionCheckController {

    private final String UPDATE_VERSION = "0.0.1";
    private final String UPDATE_URL = "144.24.81.53:8080/";

    /**
     * 현재 강제로 업그레이드 해야할 버전 알림
     */
    @GetMapping("")
    public ResponseEntity<?> getForcedUpdateVersionInfo() {
        try {
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, new VersionInfo(UPDATE_VERSION, UPDATE_URL));
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    @Getter
    private class VersionInfo {
        private String updateVersion;
        private String updateURL;

        public VersionInfo(String updateVersion, String updateURL) {
            this.updateVersion = updateVersion;
            this.updateURL = updateURL;
        }
    }
}
