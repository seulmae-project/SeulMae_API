package com.seulmae.seulmae.announcement.controller;

import com.seulmae.seulmae.announcement.dto.request.AddAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.request.UpdateAnnouncementRequest;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementDetailResponse;
import com.seulmae.seulmae.announcement.dto.response.AnnouncementMainListResponse;
import com.seulmae.seulmae.announcement.service.AnnouncementService;
import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.ErrorCode;
import com.seulmae.seulmae.global.util.enums.ErrorResponse;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcement/v1")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    // 게시판 생성()
    @PostMapping("")
    public ResponseEntity<?> createAnnouncement(@RequestBody AddAnnouncementRequest request, @AuthenticationPrincipal User user) {
        try {
            announcementService.createAnnouncement(request, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    // 게시판 수정
    @PutMapping("")
    public ResponseEntity<?> updateAnnouncement(@RequestParam Long announcementId,
                                                @RequestBody UpdateAnnouncementRequest request,
                                                @AuthenticationPrincipal User user) {
        try {
            announcementService.updateAnnouncement(announcementId, request, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    // 게시판 detail
    @GetMapping("")
    public ResponseEntity<?> getAnnouncement(@RequestParam Long announcementId,
                                             @AuthenticationPrincipal User user) {
        try {
            AnnouncementDetailResponse result = announcementService.getAnnouncement(announcementId, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, result);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    // 게시판 리스트
    @GetMapping("/list")
    public ResponseEntity<?> getAnnouncements(@RequestParam Long workplaceId,
                                              @RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "5") Integer size,
                                              @AuthenticationPrincipal User user) {
        try {
            Object results = announcementService.getAnnouncements(workplaceId, user, page, size);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, results);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    @GetMapping("/list/important")
    public ResponseEntity<?> getImportantAnnouncements(@RequestParam Long workplaceId,
                                                       @AuthenticationPrincipal User user) {
        try {
            List<AnnouncementMainListResponse> results = announcementService.getImportantAnnouncements(workplaceId, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, results);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }

    @GetMapping("/main")
    public ResponseEntity<?> getMainAnnouncements(@RequestParam Long workplaceId,
                                                  @AuthenticationPrincipal User user) {
        try {
            List<AnnouncementMainListResponse> results = announcementService.getMainAnnouncements(workplaceId, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, results);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }


    // 게시판 삭제
    @DeleteMapping("")
    public ResponseEntity<?> deleteAnnouncement(@RequestParam Long announcementId,
                                                @AuthenticationPrincipal User user) {
        try {
            announcementService.deleteAnnouncement(announcementId, user);
            return ResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        }
    }
}
