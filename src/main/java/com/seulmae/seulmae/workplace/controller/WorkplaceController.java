package com.seulmae.seulmae.workplace.controller;

import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceListInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceModifyDto;
import com.seulmae.seulmae.workplace.service.WorkplaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        workplaceService.addWorkplace(workplaceAddDto, multipartFileList, user);
        SuccessResponse successResponse = new SuccessResponse(SuccessCode.INSERT_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    /**
     * 근무지 전체 리스트
     */
    @GetMapping("info/all")
    public ResponseEntity<?> getAllWorkplace(HttpServletRequest request) {
        List<WorkplaceListInfoDto> workplaceListInfoDtoList = workplaceService.getAllWorkplace(request);
        SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, workplaceListInfoDtoList);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    /**
     * 근무지 상세보기
     * @param workplaceId
     */
    @GetMapping("info")
    public ResponseEntity<?> getSpecificWorkplace(@RequestParam Long workplaceId, HttpServletRequest request) {
        WorkplaceInfoDto workplaceInfoDto = workplaceService.getSpecificWorkplace(workplaceId, request);
        SuccessResponse successResponse = new SuccessResponse(SuccessCode.SELECT_SUCCESS, workplaceInfoDto);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    /**
     * 근무지 수정
     * @param workplaceModifyDto
     * @param multipartFileList
     */
    @PatchMapping("modify")
    public ResponseEntity<?> modifyWorkplace(@RequestPart WorkplaceModifyDto workplaceModifyDto,
                                             @RequestPart(required = false) List<MultipartFile> multipartFileList) throws IOException {
        workplaceService.modifyWorkplace(workplaceModifyDto, multipartFileList);
        SuccessResponse successResponse = new SuccessResponse(SuccessCode.UPDATE_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    /**
     * 근무지 삭제
     * @param workplaceId
     */
    @DeleteMapping("delete")
    public ResponseEntity<?> modifyWorkplace(@RequestParam Long workplaceId) {
        workplaceService.deleteWorkplace(workplaceId);
        SuccessResponse successResponse = new SuccessResponse(SuccessCode.DELETE_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}