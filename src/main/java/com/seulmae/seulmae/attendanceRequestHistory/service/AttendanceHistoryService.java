package com.seulmae.seulmae.attendanceRequestHistory.service;

import com.seulmae.seulmae.attendanceRequestHistory.dto.*;
import com.seulmae.seulmae.user.entity.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceHistoryService {
    List<AttendanceCalendarDto> getEmployeeCalendar(User user, Long workplaceId, Integer year, Integer month);

    List<AttendanceCalendarDto> getManagerCalendar(User user, Long workplaceId, LocalDate todayDate);

    WorkStatusDto getStatus(User user, Long workplaceId);

    MonthlyWorkSummaryDto getMonthlyWork(User user, Long workplaceId, Integer year, Integer month);

    Page<AttendanceRequestHistoryDto> getHistoryList(User user, Long workplaceId, Integer year, Integer month, Integer page, Integer size);

    AttendanceRequestHistoryDetailDto getHistoryDetail(User user, Long idAttendanceRequestHistory);
}
