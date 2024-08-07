package com.seulmae.seulmae.attendanceRequestHistory.service;

import com.seulmae.seulmae.attendanceRequestHistory.dto.*;
import com.seulmae.seulmae.user.entity.User;

public interface AttendanceHistoryService {
    AttendanceCalendarDto getCalender(User user, Long workplaceId, Integer year, Integer month);

    WorkStatusDto getStatus(User user, Long workplaceId);

    MonthlyWorkSummaryDto getMonthlyWork(User user, Long workplaceId, Integer year, Integer month);

    AttendanceRequestHistoryDto getHistoryList(User user, Long workplaceId, Integer year, Integer month, Integer page, Integer size);

    AttendanceRequestHistoryDetailDto getHistoryDetail(User user, Long idAttendanceRequestHistory);

}
