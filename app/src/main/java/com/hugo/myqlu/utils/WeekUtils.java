package com.hugo.myqlu.utils;

/**
 * @auther Hugo
 * Created on 2016/4/14 16:10.
 */
public class WeekUtils {
    private static String mWay;

    public static String getWeek(int id) {
        mWay = String.valueOf(id);
        if ("1".equals(mWay)) {
            mWay = "日";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("0".equals(mWay)) {
            mWay = "六";
        }
        return "周" + mWay;
    }


}
