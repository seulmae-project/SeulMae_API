package com.seulmae.seulmae.attendance.service;

import com.seulmae.seulmae.attendance.dto.AttendanceRequestHistoryDto;
import com.seulmae.seulmae.user.entity.User;

public interface AttendanceHistoryService {
    AttendanceRequestHistoryDto getHistoryList(User user, Long workplaceId, int month, int year, int page, int size);
}
