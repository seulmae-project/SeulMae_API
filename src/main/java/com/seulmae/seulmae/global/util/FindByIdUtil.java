package com.seulmae.seulmae.global.util;

import com.seulmae.seulmae.attendance.entity.Attendance;
import com.seulmae.seulmae.attendance.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.attendance.repository.AttendanceRepository;
import com.seulmae.seulmae.attendance.repository.AttendanceRequestHistoryRepository;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.entity.WorkplaceApprove;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceApproveRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceJoinHistoryRepository;
import com.seulmae.seulmae.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FindByIdUtil {

    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceApproveRepository workplaceApproveRepository;
    private final WorkplaceJoinHistoryRepository workplaceJoinHistoryRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRequestHistoryRepository attendanceRequestHistoryRepository;

    public Workplace getWorkplaceById(Long workplaceId) {
        return workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new NullPointerException("This workplaceId doesn't exist."));
    }

    public WorkplaceApprove getWorkplaceApproveById(Long workplaceApproveId) {
        return workplaceApproveRepository.findById(workplaceApproveId)
                .orElseThrow(() -> new NullPointerException("This workplaceApproveId doesn't exist."));
    }

    public WorkplaceJoinHistory getWorkplaceJoinHistoryById(Long workplaceJoinHistoryId) {
        return workplaceJoinHistoryRepository.findById(workplaceJoinHistoryId)
                .orElseThrow(() -> new NullPointerException("This workplaceJoinHistoryId doesn't exist."));
    }

    public Attendance getAttendanceById(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new NullPointerException("This attendanceId doesn't exist."));
    }

    public AttendanceRequestHistory getAttendanceRequestHistoryById(Long attendanceRequestHistoryId) {
        return attendanceRequestHistoryRepository.findById(attendanceRequestHistoryId)
                .orElseThrow(() -> new NullPointerException("This attendanceRequestHistoryId doesn't exist."));
    }

    public WorkSchedule getWorkScheduleById(Long workScheduleId) {
        return workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 근무일정 ID가 존재하지 않습니다."));
    }
}
