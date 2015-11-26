package com.applozic.mobicommons.commons.core.utils;

import android.os.SystemClock;

import net.mobitexter.mobiframework.commons.core.utils.SntpClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by devashish on 28/11/14.
 */
public class DateUtils {


    public static boolean isSameDay(Long timestamp) {
        Calendar calendarForCurrent = Calendar.getInstance();
        Calendar calendarForScheduled = Calendar.getInstance();
        Date currentDate = new Date();
        Date date = new Date(timestamp);
        calendarForCurrent.setTime(currentDate);
        calendarForScheduled.setTime(date);
        return calendarForCurrent.get(Calendar.YEAR) == calendarForScheduled.get(Calendar.YEAR) &&
                calendarForCurrent.get(Calendar.DAY_OF_YEAR) == calendarForScheduled.get(Calendar.DAY_OF_YEAR);
    }

    public static String getFormattedDate(Long timestamp) {
        boolean sameDay = isSameDay(timestamp);
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM");
        return sameDay ? simpleDateFormat.format(date) : fullDateFormat.format(date) + " " + simpleDateFormat.format(date);
    }

    public static long getTimeDiffFromUtc() {
        SntpClient sntpClient = new SntpClient();
        long diff = 0;
        if (sntpClient.requestTime("0.africa.pool.ntp.org", 30000)) {
            long utcTime = sntpClient.getNtpTime() + SystemClock.elapsedRealtime() - sntpClient.getNtpTimeReference();
            diff = utcTime - System.currentTimeMillis();
        }
        return diff;
    }

    public static String getFormattedDateAndTime(Long timestamp) {
        boolean sameDay = isSameDay(timestamp);
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM");
        Date newDate = new Date();

        if (sameDay) {
            long currentTime = newDate.getTime() - date.getTime();
            long diffMinutes = currentTime / (60 * 1000) % 60;
            long diffHours = currentTime / (60 * 60 * 1000) % 24;

            if (diffMinutes <= 2 && diffHours == 0) {
                return "Just now";
            }
            if (diffMinutes <= 59 && diffHours == 0) {
                return String.valueOf(diffMinutes) + " mins";
            }
            if (diffHours <= 2) {
                return String.valueOf(diffHours) + "h";
            }
            return simpleDateFormat.format(date);
        } else {
            return fullDateFormat.format(date);
        }
    }

    public static String getDateAndTimeForLastSeen(Long timestamp) {
        boolean sameDay = isSameDay(timestamp);
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy,hh:mm aa");
       // SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM");
        Date newDate = new Date();

        if (sameDay) {
            long currentTime = newDate.getTime() - date.getTime();
            long diffMinutes = currentTime / (60 * 1000) % 60;
            long diffHours = currentTime / (60 * 60 * 1000) % 24;

            if (diffMinutes <= 1 && diffHours == 0) {
                return "Just now";
            }
            if (diffMinutes <= 59 && diffHours == 0) {
                return String.valueOf(diffMinutes) + " mins ago";
            }
            if (diffHours <= 24) {
                return String.valueOf(diffHours) + " hrs ago";
            }
        }
        return simpleDateFormat.format(date) + " ago ";
    }

}
