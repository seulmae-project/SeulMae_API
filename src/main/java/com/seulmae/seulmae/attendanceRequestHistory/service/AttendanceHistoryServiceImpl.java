package com.seulmae.seulmae.attendanceRequestHistory.service;

import com.seulmae.seulmae.attendance.repository.AttendanceRepository;
import com.seulmae.seulmae.attendanceRequestHistory.dto.*;
import com.seulmae.seulmae.attendanceRequestHistory.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.attendanceRequestHistory.repository.AttendanceRequestHistoryRepository;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.global.util.FindByUserAndWorkPlaceUtil;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.workplace.entity.Workplace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class AttendanceHistoryServiceImpl implements AttendanceHistoryService {
    private final AttendanceRequestHistoryRepository attendanceRequestHistoryRepository;
    private final AttendanceRepository attendanceRepository;

    private final FindByIdUtil findByIdUtil;
    private final FindByUserAndWorkPlaceUtil findByUserAndWorkPlaceUtil;

    /*
     * 근무 달력 조회
     * 주어진 근무지 ID와 년, 월을 사용하여 근무 달력을 반환합니다.
     */
    @Override
    public List<AttendanceCalendarDto> getCalender(User user, Long workplaceId, Integer year, Integer month) {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<AttendanceCalendarDto> attendanceCalendarDtoList = attendanceRequestHistoryRepository
                .findByUserAndWorkplaceAndDateBetween(user.getIdUser(), workplaceId, startDate, endDate);
//                .orElseThrow(() -> new NoSuchElementException("조건에 맞는 근무요청이 없습니다."));

        return attendanceCalendarDtoList;
    }

    /*
     * 근무 현황 조회
     * 주어진 근무지 ID를 사용하여 근무 현황을 반환합니다.
     */
    @Override
    public WorkStatusDto getStatus(User user, Long workplaceId) {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);
        Wage wage = findByUserAndWorkPlaceUtil.getWageByUserAndWorkPlace(user, workplace);

        // 근무 현황 데이터 조회 및 가공
        LocalDate firstWorkDate = attendanceRepository.findFirstWorkDateByUserIdAndWorkplaceId(user.getIdUser(), workplaceId)
                .orElseThrow(() -> new NoSuchElementException("첫 근무 정보가 없습니다."));
        long workedDays = DAYS.between(firstWorkDate, LocalDate.now());
        Integer payday = wage.getPayday();

        // WorkStatusDto 객체 생성 및 반환
        return new WorkStatusDto(workedDays, firstWorkDate, payday);
    }

    /*
     * 월간 근무 요약 조회
     * 주어진 근무지 ID와 년, 월을 사용하여 월간 근무 요약을 반환합니다.
     */
    @Override
    public MonthlyWorkSummaryDto getMonthlyWork(User user, Long workplaceId, Integer year, Integer month) {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);
        Wage wage = findByUserAndWorkPlaceUtil.getWageByUserAndWorkPlace(user, workplace);

        LocalDate applyStartDate = LocalDate.of(year, month, 1);
        LocalDate applyEndDate = applyStartDate.withDayOfMonth(applyStartDate.lengthOfMonth());
        Integer baseWage = wage.getBaseWage();
        BigDecimal monthlyWorkTime = attendanceRequestHistoryRepository.sumMonthlyWorkTime(user.getIdUser(), workplaceId, year, month);

        Integer monthlyWage = attendanceRepository.sumConfirmedWage(user.getIdUser(), workplaceId, year, month);

        // MonthlyWorkSummaryDto 객체 생성 및 반환
        return new MonthlyWorkSummaryDto(monthlyWage, applyStartDate, applyEndDate, baseWage, monthlyWorkTime);
    }

    /*
     * 근무 이력 리스트 조회
     * 주어진 근무지 ID와 년, 월, 페이지 정보를 사용하여 근무 이력 리스트를 반환합니다.
     */
    @Override
    public Page<AttendanceRequestHistoryDto> getHistoryList(User user, Long workplaceId, Integer year, Integer month, Integer page, Integer size) {
        Workplace workplace = findByIdUtil.getWorkplaceById(workplaceId);

        // 페이지 및 사이즈 정보를 사용하여 이력 리스트 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceRequestHistory> historyPage = attendanceRequestHistoryRepository.findByWorkplaceIdAndYearAndMonth(workplaceId, year, month, pageable);

        // 변환된 페이지 반환
        return historyPage.map(history -> new AttendanceRequestHistoryDto(
                history.getAttendance().getWorkDate(),
                history.getWorkStartTime(),
                history.getWorkEndTime(),
                history.getTotalWorkTime(),
                history.getAttendance().getConfirmedWage(),
                history.getIsRequestApprove(),
                history.getIsManagerCheck(),
                history.getIdAttendanceRequestHistory()));
    }

    /*
     * 근무 이력 상세 조회
     * 주어진 근무 이력 ID를 사용하여 근무 이력 상세 정보를 반환합니다.
     */
    @Override
    public AttendanceRequestHistoryDetailDto getHistoryDetail(User user, Long idAttendanceRequestHistory) {
        AttendanceRequestHistory history = attendanceRequestHistoryRepository.findById(idAttendanceRequestHistory)
                .orElseThrow(() -> new NoSuchElementException("해당 근무 이력 ID가 존재하지 않습니다."));

        // AttendanceRequestHistoryDetailDto 객체 생성 및 반환
        return new AttendanceRequestHistoryDetailDto(
                history.getAttendance().getConfirmedWage(),
                history.getDeliveryMessage(),
                history.getAttendanceRequestMemo()
        );
    }
}
