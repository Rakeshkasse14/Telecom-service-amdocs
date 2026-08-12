package com.amdocs.telecom.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public static String calculateDeadline(int hoursAhead) {
        return LocalDateTime.now().plusHours(hoursAhead).format(FORMATTER);
    }

    public static String calculateDeadlineMinutes(int minutesAhead) {
        return LocalDateTime.now().plusMinutes(minutesAhead).format(FORMATTER);
    }

    public static long minutesRemaining(String deadlineStr) {
        try {
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr, FORMATTER);
            return ChronoUnit.MINUTES.between(LocalDateTime.now(), deadline);
        } catch (Exception e) {
            return 0;
        }
    }
}
