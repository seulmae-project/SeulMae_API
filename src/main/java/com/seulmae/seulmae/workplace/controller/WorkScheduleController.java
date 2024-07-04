package com.seulmae.seulmae.workplace.controller;

import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.dto.WorkScheduleAddDto;
import com.seulmae.seulmae.workplace.dto.WorkScheduleInfoDto;
import com.seulmae.seulmae.workplace.dto.WorkScheduleUpdateDto;
import com.seulmae.seulmae.workplace.service.WorkScheduleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule/v1")
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;

    // 일정 추가
    @PostMapping("")
    public ResponseEntity<?> addWorkSchedule(@RequestBody WorkScheduleAddDto workScheduleAddDto,
                                             @AuthenticationPrincipal User user) {
        workScheduleService.createWorkSchedule(workScheduleAddDto, user);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.INSERT_SUCCESS), HttpStatus.CREATED);
    }
    // 일정 수정
    @PutMapping("")
    public ResponseEntity<?> updateWorkSchedule(@RequestParam Long workScheduleId,
                                                @RequestBody WorkScheduleUpdateDto workScheduleUpdateDto,
                                                @AuthenticationPrincipal User user) {
        workScheduleService.updateWorkSchedule(workScheduleId, workScheduleUpdateDto, user);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.UPDATE_SUCCESS), HttpStatus.CREATED);
    }

    // 일정 삭제
    @DeleteMapping("")
    public ResponseEntity<?> deleteWorkSchedule(@RequestParam Long workScheduleId,
                                                @AuthenticationPrincipal User user) {
        workScheduleService.deleteWorkSchedule(workScheduleId, user);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.DELETE_SUCCESS), HttpStatus.OK);

    }

    // 일정 상세 조회

    @GetMapping("")
    public ResponseEntity<?> getWorkSchedule(@RequestParam Long workScheduleId,
                                             @AuthenticationPrincipal User user) {
     WorkScheduleInfoDto result = workScheduleService.getWorkSchedule(workScheduleId, user);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SELECT_SUCCESS, result), HttpStatus.OK);
    }

    // 일정 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<?> getWorkSchedules(@RequestParam Long workplaceId,
                                              @AuthenticationPrincipal User user) {
        List<WorkScheduleInfoDto> results = workScheduleService.getWorkSchedules(workplaceId, user);
        return new ResponseEntity<>(new SuccessResponse(SuccessCode.SELECT_SUCCESS, results), HttpStatus.OK);

    }
}
