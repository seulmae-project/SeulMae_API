package com.seulmae.seulmae.attendanceRequestHistory.service;

import com.seulmae.seulmae.attendanceRequestHistory.dto.*;
import com.seulmae.seulmae.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceHistoryServiceImpl implements AttendanceHistoryService {

    @Override
    public AttendanceCalendarDto getCalender(User user, Long workplaceId, Integer year, Integer month) {
        return null;
    }

    @Override
    public WorkStatusDto getStatus(User user, Long workplaceId) {
        return null;
    }

    @Override
    public MonthlyWorkSummaryDto getMonthlyWork(User user, Long workplaceId, Integer year, Integer month) {
        return null;
    }

    @Override
    public AttendanceRequestHistoryDto getHistoryList(User user, Long workplaceId, Integer year, Integer month, Integer page, Integer size) {
        return null;
    }

    @Override
    public AttendanceRequestHistoryDetailDto getHistoryDetail(User user, Long idAttendanceRequestHistory) {
        return null;
    }
}
