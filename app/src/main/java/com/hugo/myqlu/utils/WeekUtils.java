package com.hugo.myqlu.utils;

/**
 * @author Hugo
 * Created on 2016/5/22 10:19.
 */
public class WeekUtils {
    public static String setTime(String time) {
        if (time.equals("周一")) {
            time = "1";
        } else if (time.equals("周二")) {
            time = "2";
        } else if (time.equals("周三")) {
            time = "3";
        } else if (time.equals("周四")) {
            time = "4";
        } else if (time.equals("周五")) {
            time = "5";
        } else if (time.equals("周六")) {
            time = "6";
        } else if (time.equals("周日")) {
            time = "7";
        }
        return time;
    }

    public static String setWeek(String time) {
        if (time.equals("2")) {
            time = "周一";
        } else if (time.equals("3")) {
            time = "周二";
        } else if (time.equals("4")) {
            time = "周三";
        } else if (time.equals("5")) {
            time = "周四";
        } else if (time.equals("6")) {
            time = "周五";
        } else if (time.equals("7")) {
            time = "周六";
        } else if (time.equals("1")) {
            time = "周日";
        }
        return time;
    }

    public static String getWeek(String time) {
        if (time.equals("1")) {
            time = "周一";
        } else if (time.equals("2")) {
            time = "周二";
        } else if (time.equals("3")) {
            time = "周三";
        } else if (time.equals("4")) {
            time = "周四";
        } else if (time.equals("5")) {
            time = "周五";
        } else if (time.equals("6")) {
            time = "周六";
        } else if (time.equals("7")) {
            time = "周日";
        }
        return time;
    }
}
