package com.seulmae.seulmae.attendance.service;

import com.seulmae.seulmae.attendance.dto.AttendanceApprovalDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto;
import com.seulmae.seulmae.attendance.entity.Attendance;
import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.attendance.repository.AttendanceRepository;
import com.seulmae.seulmae.attendance.repository.AttendanceRequestHistoryRepository;
import com.seulmae.seulmae.global.exception.AttendanceRequestConflictException;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceRequestHistoryRepository attendanceRequestHistoryRepository;
    private final FindByIdUtil findByIdUtil;

    @Transactional
    public void goToWork(User user, Long workplaceId) {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);

        Attendance attendance = Attendance.builder()
                .user(user)
                .workplace(workplace)
                .workDate(LocalDate.now())
                .confirmedWage(0)
                .unconfirmedWage(0)
                .build();

        attendanceRepository.save(attendance);

        AttendanceRequestHistory attendanceRequestHistory = AttendanceRequestHistory.builder()
                .attendance(attendance)
                .workStartTime(LocalDateTime.now())
                .isRequestApprove(false)
                .isManagerCheck(false)
                .build();

        attendanceRequestHistoryRepository.save(attendanceRequestHistory);
    }

    @Transactional
    public void sendAttendanceRequest(User user, AttendanceRequestDto attendanceRequestDto) {
        Workplace workplace = findByIdUtil.getWorkplaceById(attendanceRequestDto.getWorkplaceId());

        Attendance attendance = Attendance.builder()
                .user(user)
                .workplace(workplace)
                .workDate(attendanceRequestDto.getWorkDate())
                .confirmedWage(0)
                .unconfirmedWage(attendanceRequestDto.getUnconfirmedWage())
                .build();

        attendanceRepository.save(attendance);

        AttendanceRequestHistory attendanceRequestHistory = AttendanceRequestHistory.builder()
                .attendance(attendance)
                .workStartTime(attendanceRequestDto.getWorkStartTime())
                .workEndTime(attendanceRequestDto.getWorkEndTime())
                .totalWorkTime(attendanceRequestDto.getTotalWorkTime())
                .isRequestApprove(false)
                .isManagerCheck(false)
                .deliveryMessage(attendanceRequestDto.getDeliveryMessage())
                .build();

        attendanceRequestHistoryRepository.save(attendanceRequestHistory);

        /** 매니저에게 알림 **/
    }

    @Transactional
    public void sendAttendanceApproval(AttendanceApprovalDto attendanceApprovalDto) {
        AttendanceRequestHistory attendanceRequestHistory = findByIdUtil.getAttendanceRequestHistoryById(attendanceApprovalDto.getAttendanceRequestHistoryId());
        Attendance attendance = attendanceRequestHistory.getAttendance();

        attendance.setConfirmedWage(attendanceApprovalDto.getConfirmedWage());
        attendance.setUnconfirmedWage(0);

        attendanceRepository.save(attendance);

        attendanceRequestHistory.setAttendance(attendance);
        attendanceRequestHistory.setIsManagerCheckTrueAndCheckDate();
        attendanceRequestHistory.setIsRequestApproveTrue();

        attendanceRequestHistoryRepository.save(attendanceRequestHistory);
        
        /** 승인 알림 **/
    }

    @Transactional
    public void sendAttendanceRejection(Long attendanceRequestHistoryId) {
        AttendanceRequestHistory attendanceRequestHistory = findByIdUtil.getAttendanceRequestHistoryById(attendanceRequestHistoryId);
        Attendance attendance = attendanceRequestHistory.getAttendance();

        attendance.setUnconfirmedWage(0);

        attendanceRepository.save(attendance);

        attendanceRequestHistory.setAttendance(attendance);
        attendanceRequestHistory.setIsManagerCheckTrueAndCheckDate();

        attendanceRequestHistoryRepository.save(attendanceRequestHistory);

        /** 거절 알림 **/
    }

    @Transactional
    public List<AttendanceRequestListDto> getAttendanceRequestList(Long workplaceId) {
        List<AttendanceRequestListDto> attendanceRequestListDtoList = attendanceRepository.findByWorkplaceId(workplaceId);

        return attendanceRequestListDtoList;
    }

    @Transactional
    public void sendSeparateAttendanceRequest(User user, AttendanceRequestDto attendanceRequestDto) {
        Boolean existByUserAndWorkDate = attendanceRequestHistoryRepository.existByUserAndWorkDate(
                user,
                attendanceRequestDto.getWorkDate(),
                attendanceRequestDto.getWorkStartTime(),
                attendanceRequestDto.getWorkEndTime()
        );

        if (existByUserAndWorkDate) {
            throw new AttendanceRequestConflictException("등록하려는 시간과 동일한 승인된 근무 또는 처리되지 않은 근무 요청이 존재합니다. 다시 입력해주세요.");
        }

        sendAttendanceRequest(user, attendanceRequestDto);
    }

}
