package com.seulmae.seulmae.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtil {

    // LocalDateTime을 "yyyy-MM-dd'T'HH:mm:ss" 형식의 문자열로 변환하는 메서드
    public static String formatToDateTimeString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return dateTime.format(formatter);
    }
}
