package com.example.olapark.nav.parks;

import android.os.Build;

import java.time.LocalDateTime;

public class DateTimeUtils {

    public static boolean isWithinLast30Minutes(LocalDateTime dateTime) {
        LocalDateTime now = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        LocalDateTime thirtyMinutesAgo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            thirtyMinutesAgo = now.minusMinutes(30);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return dateTime.isAfter(thirtyMinutesAgo) && dateTime.isBefore(now);
        }
        return false;
    }
}
