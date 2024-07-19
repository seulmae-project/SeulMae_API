package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.workplace.Day;
import com.seulmae.seulmae.workplace.entity.WorkScheduleDay;
import com.seulmae.seulmae.workplace.entity.WorkplaceJoinHistory;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class UserInfoWithWorkplaceResponse {

    private String name;
    private String phoneNumber;
    private String imageURL;
    private String joinDate;

    private Long workScheduleId;
    private String workScheduleTitle;
    private List<Integer> days;

    private Integer payDay;
    private Integer baseWage;
    private String memo;

    public UserInfoWithWorkplaceResponse(UserWorkplace userWorkplace, UserWorkSchedule userWorkSchedule, Wage wage, WorkplaceJoinHistory workplaceJoinHistory, String imageURL) {
        this.name = userWorkplace.getUser().getName();
        this.phoneNumber = userWorkplace.getUser().getPhoneNumber();
        this.imageURL = imageURL;
        this.joinDate = workplaceJoinHistory.getDecisionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.workScheduleId = userWorkSchedule.getWorkSchedule().getIdWorkSchedule();
        this.workScheduleTitle = userWorkSchedule.getWorkSchedule().getWorkScheduleTitle();
        this.days = makeIntegerFromWorkScheduleDay(userWorkSchedule.getWorkSchedule().getWorkScheduleDays());
        this.payDay = wage.getPayday();
        this.baseWage = wage.getBaseWage();
        this.memo = userWorkplace.getMemo();
    }

    private List<Integer> makeIntegerFromWorkScheduleDay(List<WorkScheduleDay> workScheduleDays) {
        return workScheduleDays.stream()
                .map(workScheduleDay -> Day.fromDay(workScheduleDay.getDay()))
                .toList();
    }
}
