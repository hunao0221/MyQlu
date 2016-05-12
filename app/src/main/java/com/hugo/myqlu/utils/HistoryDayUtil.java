package com.hugo.myqlu.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 获得查询历史消费的起始日期
 *
 * @auther Hugo
 * Created on 2016/5/5 14:31.
 */

public class HistoryDayUtil {
    //结束日期
    public static String getEndDate() {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String format = sdf.format(strDate.getTime());
        return format;
    }

    //开始日期
    public static String getStartDate(int count) {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.DATE, -count);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String format = sdf.format(strDate.getTime());
        return format;
    }

    //按月份查询时的开始日期
    public static String getMonthStart(int count) {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.MONTH, -count);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        strDate.set(Calendar.DAY_OF_MONTH, 1);
        String format = sdf.format(strDate.getTime());
        return format;
    }

    //按月份查询的结束日期
    public static String getMonthEnd(int count) {
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.MONTH, -count);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        strDate.set(Calendar.DAY_OF_MONTH, strDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        String format = sdf.format(strDate.getTime());
        return format;
    }

}
