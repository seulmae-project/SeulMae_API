package com.seulmae.seulmae.attendance.service;

import com.seulmae.seulmae.attendance.dto.AttendanceApprovalDto;
import com.seulmae.seulmae.attendance.dto.AttendanceManagerMainListDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestDto;
import com.seulmae.seulmae.attendance.dto.AttendanceRequestListDto;
import com.seulmae.seulmae.attendance.entity.Attendance;
import com.seulmae.seulmae.attendance.repository.AttendanceRepository;
import com.seulmae.seulmae.attendanceRequestHistory.entity.AttendanceRequestHistory;
import com.seulmae.seulmae.attendanceRequestHistory.repository.AttendanceRequestHistoryRepository;
import com.seulmae.seulmae.global.exception.AttendanceRequestConflictException;
import com.seulmae.seulmae.global.util.FindByIdUtil;
import com.seulmae.seulmae.global.util.UrlUtil;
import com.seulmae.seulmae.notification.NotificationType;
import com.seulmae.seulmae.notification.event.MultiDeviceNotificationEvent;
import com.seulmae.seulmae.notification.service.NotificationService;
import com.seulmae.seulmae.user.entity.User;
import com.seulmae.seulmae.user.entity.UserImage;
import com.seulmae.seulmae.user.repository.UserImageRepository;
import com.seulmae.seulmae.user.repository.UserWorkplaceRepository;
import com.seulmae.seulmae.user.service.UserWorkplaceService;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.entity.WorkSchedule;
import com.seulmae.seulmae.workplace.entity.Workplace;
import com.seulmae.seulmae.workplace.repository.WorkScheduleRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceRequestHistoryRepository attendanceRequestHistoryRepository;
    private final UserWorkplaceRepository userWorkplaceRepository;
    private final UserImageRepository userImageRepository;
    private final UserWorkplaceService userWorkplaceService;
    private final WorkScheduleRepository workScheduleRepository;

    private final FindByIdUtil findByIdUtil;
    private final NotificationService notificationService;

    private final ApplicationEventPublisher eventPublisher;

    @Value("${file.endPoint.user}")
    private String userImageEndPoint;


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

        userWorkplaceService.checkWorkplaceAuthority(workplace, user);
        /** 출퇴근 요청 중복 유효성 추가 **/
        LocalTime workStartTime = attendanceRequestDto.getWorkStartTime().toLocalTime();
        LocalTime workEndTime = attendanceRequestDto.getWorkEndTime().toLocalTime();
        Day day = Day.fromInt(attendanceRequestDto.getDay());

        if (checkIfWithinTimeRange(user, workplace, day, attendanceRequestDto.getDay(), workStartTime, workEndTime)) {
            autoApprove(user, workplace, attendanceRequestDto);
        } else {
            checkDuplicateAttendanceRequest(user, attendanceRequestDto);
          
            sendRequestToManager(user, workplace, attendanceRequestDto);
        }

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
        String title = "[출퇴근 승인 알림]";
        String body = "'" + attendance.getWorkplace().getWorkplaceName() + "'의 [" + attendance.getWorkDate() + "]에 대한 출퇴근 요청이 승인되었습니다.";
        eventPublisher.publishEvent(new MultiDeviceNotificationEvent(title, body, attendance.getUser(), NotificationType.ATTENDANCE_RESPONSE, attendanceRequestHistory.getIdAttendanceRequestHistory(), attendance.getWorkplace().getIdWorkPlace()));
//        notificationService.sendMessageToUserWithMultiDevice(title, body, attendance.getUser(), NotificationType.ATTENDANCE_RESPONSE, attendanceRequestHistory.getIdAttendanceRequestHistory(), attendance.getWorkplace().getIdWorkPlace());
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
        String title = "[출퇴근 거절 알림]";
        String body = "'" + attendance.getWorkplace().getWorkplaceName() + "'의 [" + attendance.getWorkDate() + "]에 대한 출퇴근 요청이 거절되었습니다. 거절사유는 매니저에게 직접 문의바랍니다.";
        eventPublisher.publishEvent(new MultiDeviceNotificationEvent(title, body, attendance.getUser(), NotificationType.ATTENDANCE_RESPONSE, attendanceRequestHistory.getIdAttendanceRequestHistory(), attendance.getWorkplace().getIdWorkPlace()));
//        notificationService.sendMessageToUserWithMultiDevice(title, body, attendance.getUser(), NotificationType.ATTENDANCE_RESPONSE, attendanceRequestHistory.getIdAttendanceRequestHistory(), attendance.getWorkplace().getIdWorkPlace());
    }

    @Transactional
    public List<AttendanceRequestListDto> getAttendanceRequestList(Long workplaceId) {
        List<AttendanceRequestListDto> attendanceRequestListDtoList = attendanceRepository.findByWorkplaceId(workplaceId);

        return attendanceRequestListDtoList;
    }

    @Transactional
    public void sendSeparateAttendanceRequest(User user, AttendanceRequestDto attendanceRequestDto) {
        Workplace workplace = findByIdUtil.getWorkplaceById(attendanceRequestDto.getWorkplaceId());

        userWorkplaceService.checkWorkplaceAuthority(workplace, user);

        /** 중복 등록 유효성 **/
        checkDuplicateAttendanceRequest(user, attendanceRequestDto);

        sendRequestToManager(user, workplace, attendanceRequestDto);
    }

    @Transactional
    public List<AttendanceManagerMainListDto> getDailyEmployeeAttendanceList(Workplace workplace, LocalDate localDate, HttpServletRequest httpServletRequest) {
        List<AttendanceManagerMainListDto> attendanceManagerMainListDtoList = attendanceRequestHistoryRepository.findByWorkplaceAndDate(workplace, localDate);

        attendanceManagerMainListDtoList = attendanceManagerMainListDtoList.stream()
                .map(attendanceManagerMainListDto -> {
                    UserImage userImage = userImageRepository.findByUserId(attendanceManagerMainListDto.getUserId());
                    String userImageUrl = userImage != null ? UrlUtil.getBaseUrl(httpServletRequest) + userImageEndPoint + "?userImageId=" + userImage.getIdUserImage() : null;

                    attendanceManagerMainListDto.setUserImageUrl(userImageUrl);
                    return attendanceManagerMainListDto;
                })
                .collect(Collectors.toList());


        return attendanceManagerMainListDtoList;
    }

    public void autoApprove(User user, Workplace workplace, AttendanceRequestDto attendanceRequestDto) {
        User manager = userWorkplaceRepository.findUserByWorkplaceAndIsManager(workplace, true)
                .orElseThrow(() -> new NoSuchElementException("해당 근무지의 매니저가 존재하지 않습니다."));

        Attendance attendance = Attendance.builder()
                .user(user)
                .workplace(workplace)
                .workDate(attendanceRequestDto.getWorkDate())
                .confirmedWage(attendanceRequestDto.getConfirmedWage())
                .unconfirmedWage(0)
                .build();

        attendanceRepository.save(attendance);

        AttendanceRequestHistory attendanceRequestHistory = AttendanceRequestHistory.builder()
                .attendance(attendance)
                .workStartTime(attendanceRequestDto.getWorkStartTime())
                .workEndTime(attendanceRequestDto.getWorkEndTime())
                .totalWorkTime(attendanceRequestDto.getTotalWorkTime())
                .isRequestApprove(true)
                .isManagerCheck(false)
                .deliveryMessage(attendanceRequestDto.getDeliveryMessage())
                .build();

        attendanceRequestHistoryRepository.save(attendanceRequestHistory);

        /** 매니저에게 알림 **/
        String title = "[출퇴근자동승인]";
        String body = "'" + user.getName() + "'이 [" + attendance.getWorkDate() + "]에 대한 출퇴근 자동 승인이 되었습니다.";
        eventPublisher.publishEvent(new MultiDeviceNotificationEvent(title, body, manager, NotificationType.ATTENDANCE_REQUEST, attendanceRequestHistory.getIdAttendanceRequestHistory(), workplace.getIdWorkPlace()));
//         notificationService.sendMessageToUserWithMultiDevice(title, body, manager, NotificationType.ATTENDANCE_REQUEST, attendanceRequestHistory.getIdAttendanceRequestHistory(), workplace.getIdWorkPlace());
    }

    public void sendRequestToManager(User user, Workplace workplace, AttendanceRequestDto attendanceRequestDto) {
        User manager = userWorkplaceRepository.findUserByWorkplaceAndIsManager(workplace, true)
                .orElseThrow(() -> new NoSuchElementException("해당 근무지의 매니저가 존재하지 않습니다."));

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
        String title = "[출퇴근확인요청]";
        String body = "'" + user.getName() + "'이 [" + attendance.getWorkDate() + "]에 대한 출퇴근 확인 요청을 하였습니다.";
        eventPublisher.publishEvent(new MultiDeviceNotificationEvent(title, body, manager, NotificationType.ATTENDANCE_REQUEST, attendanceRequestHistory.getIdAttendanceRequestHistory(), workplace.getIdWorkPlace()));
//        notificationService.sendMessageToUserWithMultiDevice(title, body, manager, NotificationType.ATTENDANCE_REQUEST, attendanceRequestHistory.getIdAttendanceRequestHistory(), workplace.getIdWorkPlace());
    }

    public boolean checkIfWithinTimeRange(User user, Workplace workplace, Day day, Integer workDay, LocalTime startTime, LocalTime endTime) {
        List<WorkSchedule> schedules = workScheduleRepository.findWorkSchedulesByUserAndWorkplace(user, workplace, day);

        return schedules.stream()
                .anyMatch(schedule -> {
                    LocalTime scheduleStart = schedule.getStartTime();
                    LocalTime scheduleEnd = schedule.getEndTime();

                    return !startTime.isBefore(scheduleStart.minusMinutes(10)) && !startTime.isAfter(scheduleStart.plusMinutes(10)) &&
                            !endTime.isBefore(scheduleEnd.minusMinutes(10)) && !endTime.isAfter(scheduleEnd.plusMinutes(10));
                });
    }

    public void checkDuplicateAttendanceRequest(User user, AttendanceRequestDto attendanceRequestDto) {
        /** 승인, 자동 승인, 미승인일 경우 **/
        Boolean existByUserAndWorkDate = attendanceRequestHistoryRepository.existByUserAndWorkDate(
                user,
                attendanceRequestDto.getWorkDate(),
                attendanceRequestDto.getWorkStartTime(),
                attendanceRequestDto.getWorkEndTime()
        );

        if (existByUserAndWorkDate) {
            throw new AttendanceRequestConflictException("등록하려는 시간과 동일한 승인된 근무 또는 처리되지 않은 근무 요청이 존재합니다. 다시 입력해주세요.");
        }
    }
}
