package com.hugo.myqlu.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @auther Hugo
 * Created on 2016/5/5 14:31.
 */
public class HistoryDayUtil {
    public static String getEndDate() {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String format = sdf.format(strDate.getTime());
        return format;
    }

    public static String getStartDate(int count) {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.DATE, -count);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String format = sdf.format(strDate.getTime());
        return format;
    }

    public static String getMonthStart(int count) {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.MONTH, -count);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        strDate.set(Calendar.DAY_OF_MONTH, 1);
        String format = sdf.format(strDate.getTime());
        return format;
    }

    public static String getMonthEnd(int count) {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.MONTH, -count);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        strDate.set(Calendar.DAY_OF_MONTH, strDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        String format = sdf.format(strDate.getTime());
        return format;
    }

}
