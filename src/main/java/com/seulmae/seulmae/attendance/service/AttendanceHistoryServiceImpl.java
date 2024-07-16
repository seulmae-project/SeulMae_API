package com.seulmae.seulmae.attendance.service;

import com.seulmae.seulmae.attendance.dto.AttendanceRequestHistoryDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestHistoryDto.AttendanceRequestHistoryDetail;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestHistoryDto.MonthlyWageInfo;
import com.seulmae.seulmae.attendance.entity.Attendance;
import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.attendance.repository.AttendanceRepository;
import com.seulmae.seulmae.attendance.repository.AttendanceRequestHistoryRepository;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceHistoryServiceImpl implements AttendanceHistoryService {

    private final AttendanceRequestHistoryRepository attendanceRequestHistoryRepository;
    private final AttendanceRepository attendanceRepository;
    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceApproveRepository workplaceApproveRepository;

    @Override
    @Transactional(readOnly = true)
    public AttendanceRequestHistoryDto getHistoryList(User user, Long workplaceId, int month, int year, int page, int size) {
        // 근무지 조회
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 근무지 ID입니다."));


        // 첫 승인 일자 조회
        LocalDateTime regDateWorkplaceApprove = workplaceApproveRepository.findFirstRegDateWorkplaceApprove(user, workplace);

        // 월급 정보 조회
        MonthlyWageInfo monthlyWageInfo = fetchMonthlyWageInfo(user, workplace);

        // 근무 요청 이력 상세 정보 조회 (월별로 필터링)
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<AttendanceRequestHistory> histories = attendanceRequestHistoryRepository.findAllByUserAndWorkplaceAndWorkDateBetween(user, workplace, startDate, endDate, PageRequest.of(page, size));
        List<AttendanceRequestHistoryDetail> historyDetails = histories.stream()
                .map(history -> {
                    Attendance attendance = attendanceRepository.findByUserAndWorkplaceAndWorkDate(user, workplace, history.getRegDateAttendance())
                            .orElseThrow(() -> new IllegalArgumentException("근무 기록을 찾을 수 없습니다."));

                    Integer wage = history.getIsRequestApprove() ? attendance.getConfirmedWage() : attendance.getUnconfirmedWage();

                    return new AttendanceRequestHistoryDetail(
                            history.getRegDateAttendance(),
                            history.getWorkStartTime(),
                            history.getWorkEndTime(),
                            history.getTotalWorkTime(),
                            wage,
                            history.getDeliveryMessage(),
                            history.getAttendanceRequestMemo(),
                            history.getIsRequestApprove(),
                            history.getIsManagerCheck()
                    );
                })
                .collect(Collectors.toList());

        Page<AttendanceRequestHistoryDetail> historyDetailsPage = new PageImpl<>(historyDetails, PageRequest.of(page, size), historyDetails.size());

        return new AttendanceRequestHistoryDto(regDateWorkplaceApprove, monthlyWageInfo, historyDetailsPage);
    }

    private MonthlyWageInfo fetchMonthlyWageInfo(User user, Workplace workplace) {
        // 유저와 근무지에 대한 월급 정보
        return new MonthlyWageInfo(
                1000000, // 예시 월급
                LocalDate.of(2024, 1, 1), // 예시 시작 날짜
                LocalDate.of(2024, 12, 31), // 예시 종료 날짜
                10000, // 예시 시급
                new BigDecimal("160") // 예시 총 일한 시간(월)
        );
    }
}
