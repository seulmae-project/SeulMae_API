package com.seulmae.seulmae.user.dto.response;

import com.seulmae.seulmae.user.entity.UserWorkSchedule;
import com.seulmae.seulmae.user.entity.UserWorkplace;
import com.seulmae.seulmae.wage.entity.Wage;
import com.seulmae.seulmae.workplace.dto.WorkScheduleInfoDto;
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

    private List<WorkScheduleInfoDto> workScheduleInfoDtoList;

    private Integer payDay;
    private Integer baseWage;
    private String memo;



    public UserInfoWithWorkplaceResponse(UserWorkplace userWorkplace, List<UserWorkSchedule> userWorkScheduleList, Wage wage, WorkplaceJoinHistory workplaceJoinHistory, String imageURL) {
        this.name = userWorkplace.getUser().getName();
        this.phoneNumber = userWorkplace.getUser().getPhoneNumber();
        this.imageURL = imageURL;
        this.joinDate = workplaceJoinHistory.getDecisionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        this.workScheduleInfoDtoList = WorkScheduleInfoDto.createWorkScheduleInfoDtoList(userWorkScheduleList);

        this.payDay = wage.getPayday();
        this.baseWage = wage.getBaseWage();
        this.memo = userWorkplace.getMemo();
    }
}
