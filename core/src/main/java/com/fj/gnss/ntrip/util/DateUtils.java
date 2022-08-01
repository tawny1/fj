package com.fj.gnss.ntrip.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author:Jarven.ding
 * create:2020/3/28
 */
public class DateUtils {

    public static String convertToGGATime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss", Locale.US);
        Date date = new Date();
        date.setTime(time);
        return sdf.format(date);
    }
}
