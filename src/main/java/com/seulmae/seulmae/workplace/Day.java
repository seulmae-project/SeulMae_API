package com.seulmae.seulmae.workplace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Day {
    SUN("일요일"), MON("월요일"), TUE("화요일"), WED("수요일"),
    THU("목요일"), FRI("금요일"), SAT("토요일");

    private final String dayName;

    public static Day fromInt(int dayInt) {
        return Day.values()[dayInt];
    }

    public static int fromDay(Day day) {
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        return day.ordinal();
    }
}
