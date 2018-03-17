package com.example.a70640.firebase_example.Model;

import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by min on 2018/2/25.
 */

public class Time {

    public Time() {}

    public static String getYear(String time) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        cal.setTimeInMillis(Long.parseLong(time));
        return DateFormat.format("yy", cal).toString();
    }

    public static String getDate(String time) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        cal.setTimeInMillis(Long.parseLong(time));
        return DateFormat.format("MM-dd", cal).toString();
    }

    public static String getTime(String time) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        cal.setTimeInMillis(Long.parseLong(time));
        return DateFormat.format("HH:mm", cal).toString();
    }
}
