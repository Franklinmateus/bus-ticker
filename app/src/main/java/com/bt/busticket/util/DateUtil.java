package com.bt.busticket.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static Date getDateFromString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        format.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

        Date date = null;
        try {
            date = format.parse(dateString);
            date = format.parse(dateString);
        } catch (Exception ignore) {
        }
        return date;
    }

    public static LocalDate getLocalDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }

    public static String getDateStringFromLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public static Date getDateFromLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String asDateString(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    }
}
