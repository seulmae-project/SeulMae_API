package com.seulmae.seulmae.workplace.controller;

import com.seulmae.seulmae.global.util.ResponseUtil;
import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.user.dto.response.UserWorkplaceInfoResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceListInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceModifyDto;
import com.seulmae.seulmae.workplace.service.WorkplaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workplace/v1")
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    /**
     * 근무지 생성
     * @param workplaceAddDto
     * @param multipartFileList
     * @param user
     */
    @PostMapping("add")
    public ResponseEntity<?> addWorkplace(@RequestPart WorkplaceAddDto workplaceAddDto,
                                          @RequestPart(required = false, name = "multipartFileList") List<MultipartFile> multipartFileList,
                                          @AuthenticationPrincipal User user) {
        try {
            workplaceService.addWorkplace(workplaceAddDto, multipartFileList, user);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 전체 리스트
     */
    @GetMapping("info/all")
    public ResponseEntity<?> getAllWorkplace(HttpServletRequest request, @RequestParam(required = false) String keyword) {
        try {
            List<WorkplaceListInfoDto> workplaceListInfoDtoList = workplaceService.getAllWorkplace(request, keyword);

            return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS, workplaceListInfoDtoList);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 상세보기
     * @param workplaceId
     */
    @GetMapping("info")
    public ResponseEntity<?> getSpecificWorkplace(@RequestParam Long workplaceId, HttpServletRequest request) {
        try {
            WorkplaceInfoDto workplaceInfoDto = workplaceService.getSpecificWorkplace(workplaceId, request);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, workplaceInfoDto);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 유저별 가입 근무지 리스트
     * @param user
     */
    @GetMapping("info/join")
    public ResponseEntity<?> getJoinWorkplaceList(@AuthenticationPrincipal User user) {
        try {
            List<UserWorkplaceInfoResponse> userWorkplaceInfoResponse = workplaceService.getJoinWorkplaceList(user);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, userWorkplaceInfoResponse);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 수정
     * @param workplaceModifyDto
     * @param multipartFileList
     */
    @PatchMapping("modify")
    public ResponseEntity<?> modifyWorkplace(@RequestPart WorkplaceModifyDto workplaceModifyDto,
                                             @RequestPart(required = false) List<MultipartFile> multipartFileList) throws IOException {
        try {
            workplaceService.modifyWorkplace(workplaceModifyDto, multipartFileList);

            return ResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지 삭제
     * @param workplaceId
     */
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteWorkplace(@RequestParam Long workplaceId) {
        try {
            workplaceService.deleteWorkplace(workplaceId);

            return ResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }

    /**
     * 근무지명 중복 확인
     * @param workplaceName
     * **/
    @GetMapping("duplicate/name")
    public ResponseEntity<?> checkWorkplaceNameDuplicate(@RequestParam String workplaceName) {
        try {
            workplaceService.checkWorkplaceNameDuplicate(workplaceName);

            return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.handleException(e);
        }
    }
}