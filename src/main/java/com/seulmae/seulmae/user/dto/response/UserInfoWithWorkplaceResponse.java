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

    private WorkScheduleDto workScheduleDto;

    private Integer payDay;
    private Integer baseWage;
    private String memo;

    public UserInfoWithWorkplaceResponse(UserWorkplace userWorkplace, UserWorkSchedule userWorkSchedule, Wage wage, WorkplaceJoinHistory workplaceJoinHistory, String imageURL) {
        this.name = userWorkplace.getUser().getName();
        this.phoneNumber = userWorkplace.getUser().getPhoneNumber();
        this.imageURL = imageURL;
        this.joinDate = workplaceJoinHistory.getDecisionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        this.workScheduleDto = new WorkScheduleDto(userWorkSchedule);

        this.payDay = wage.getPayday();
        this.baseWage = wage.getBaseWage();
        this.memo = userWorkplace.getMemo();
    }

}
