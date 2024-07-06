package com.seulmae.seulmae.attendance.service;

import com.seulmae.seulmae.attendance.dto.AttendanceApprovalDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto;
import com.seulmae.seulmae.attendance.dto.GetOffWorkDto;
import com.seulmae.seulmae.attendance.entity.Attendance;
import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.attendance.repository.AttendanceRepository;
import com.seulmae.seulmae.attendance.repository.AttendanceRequestHistoryRepository;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    public void getOffWork(User user, GetOffWorkDto getOffWorkDto) {
        Workplace workplace = findByIdUtil.getWorkplaceById(getOffWorkDto.getWorkplaceId());

        Attendance attendance = Attendance.builder()
                .user(user)
                .workplace(workplace)
                .workDate(getOffWorkDto.getWorkDate())
                .confirmedWage(0)
                .unconfirmedWage(getOffWorkDto.getUnconfirmedWage())
                .build();

        attendanceRepository.save(attendance);

        AttendanceRequestHistory attendanceRequestHistory = AttendanceRequestHistory.builder()
                .attendance(attendance)
                .workStartTime(getOffWorkDto.getWorkStartTime())
                .workEndTime(getOffWorkDto.getWorkEndTime())
                .totalWorkTime(getOffWorkDto.getTotalWorkTime())
                .isRequestApprove(false)
                .isManagerCheck(false)
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

    /** 별도 요청 시 전송할 데이터의 시간과 일치하고 승인된 요청이 있다면 별도 요청 불가
     *  별도 요청 시 전송할 데이터의 시간과 일치하지만 거절된 요청이 있다면 별도 요청 가능 **/

}
